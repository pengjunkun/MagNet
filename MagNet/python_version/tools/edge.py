import random, numpy as np
import tools.const as const, tools.LRUcache as LRU

class EDGE():
  def __init__(self, class_number, size, id, lon, lat):
    self.size = size
    self.real_size = size
    self.id = id
    self.lon = lon
    self.lat = lat

    self.req_num = 0
    self.hit_num = 0

    self.cache = LRU.LRUCache(self.size)

    self.vacancy = 1.0
    self.status = const.edge_status.NORMAL
    self.mag = None

  # Add the cache capacity of the slave edge and the existing content to the master edge
  def merge_cache(self, slave_edge):
    add_size = slave_edge.cache.capacity
    self.cache.change_capacity(add_size)
    for i in range(len(slave_edge.cache)):
      vid = slave_edge.cache.popitem(last=False)[0]
      self.cache.get(vid)
    slave_edge.cache.change_capacity(-add_size)
  

  # When MAG deletes an edge, 
  # it restores the cache capacity of the slave edge, 
  # and transfers part of the contents in the master edge cache to the slave edge
  def split_cache(self, slave_edge):
    add_size = slave_edge.cache.capacity
    slave_edge.cache.change_capacity(add_size)
    for i in range(len(self.cache) // self.mag.edge_num):
      vid = self.cache.popitem(last=False)[0]
      slave_edge.cache.get(vid)
    self.cache.change_capacity(-add_size)

  def save_video(self, vid):
    self.cache.get(vid)

  def request(self, vid, vid_class = -1):
    if self.mag != None:
      self.mag.req_num += 1
      ratio_hit = False

      # Since the cache is concentrated at the master edge, 
      # the master edge is directly requested
      ratio_hit = self.mag.edge_list[0].cache.get(vid)
      if ratio_hit == True:
        self.mag.hit_num += 1
      return ratio_hit
    else:
      self.req_num += 1
      ratio_hit = self.cache.get(vid)
      if ratio_hit == True:
        self.hit_num += 1
      return ratio_hit

  # Update the busy state of the edge according to the edge idle degree
  def change_edge_status(self, tot_req, tot_size):
    self.vacancy = self.req_num * tot_size / (tot_req * self.size + 1)
    if self.vacancy > const.edge_status.BUSY_LINE:
      self.status = const.edge_status.BUSY
    elif self.vacancy > const.edge_status.NORMAL_LINE:
      self.status = const.edge_status.NORMAL
    else:
      self.status = const.edge_status.IDLE

  def get_id(self):
    return self.id

  def get_size(self):
    return self.size

  def get_real_size(self):
    return self.real_size

  def get_lon(self):
    return self.lon

  def get_lat(self):
    return self.lat

  def get_vacancy(self):
    return self.vacancy
  
  def get_status(self):
    return self.status



# mag group
class MAG():
  def __init__(self, home_edge):
    self.edge_list = [home_edge]
    self.edge_num = 1
    self.vacancy = home_edge.vacancy
    self.req_num = 0
    self.hit_num = 0
  
  # add a slave edge in mag group
  def add_edge(self, slave_edge):
    self.edge_list.append(slave_edge)
    self.edge_num += 1
    self.vacancy += slave_edge.vacancy
    self.edge_list[0].merge_cache(slave_edge)
  
  # delete the last slave edge from mag group
  def del_edge(self):
    req_num_del = self.req_num // self.edge_num
    hit_num_del = self.hit_num // self.edge_num

    self.req_num -= req_num_del
    self.hit_num -= hit_num_del

    self.edge_list[-1].req_num += req_num_del
    self.edge_list[-1].hit_num += hit_num_del

    self.vacancy -= self.edge_list[-1].vacancy
    self.edge_list[0].split_cache(self.edge_list[-1])
    self.edge_list[-1].mag = None
    self.edge_list.pop()
    self.edge_num -= 1
  
  def cal_vacancy(self):
    self.vacancy = 0
    for edge_ in self.edge_list:
      self.vacancy += edge_.vacancy
    if self.edge_num == 0:
      return 10000
    else:
      return self.vacancy / self.edge_num
    