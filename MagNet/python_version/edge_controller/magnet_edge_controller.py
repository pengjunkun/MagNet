from random import random
import numpy as np, csv, math
import tools.edge as edge,tools.tools as tools
import tools.const as const
import operator
import os


class MAGNET_EDGE_CONTROLLER():
  def __init__(self, class_number=100, edge_num=10, base_lon=116, base_lat=39.7, 
      gap=0.1, cache_size=-1, cache_size_max = 150, cache_size_min = 50, class_dict = None):
    
    self.time_p2_gap = const.default_parameter.TIME_P2_GAP
    self.time_p2_end = const.default_parameter.TIME_P2_END
    self.time_p1_gap = const.default_parameter.TIME_P1_GAP
    self.time_p1_end = const.default_parameter.TIME_P1_END

    self.edge_num = edge_num ** 2
    self.class_number = class_number
    self.cache_size = cache_size
    self.class_dict = class_dict
    
    self.tot_requests_count = 0
    self.home_edge_hit_count = 0

    self.tot_cache_size = 0

    self.p1_change_time = 0
    self.p2_change_time = 0

    self.edge_list = []
    self.edge_neighber = [[] for i in range(self.edge_num)]
    self.mag_group = []
    self.cache_class = [0 for i in range(self.class_number + 1)]
    self.cache_miss_class = [0 for i in range(self.class_number + 1)]

    self.latency = 0
    self.traffic = 0

    self.base_lon = base_lon
    self.base_lat = base_lat
    self.base = gap / edge_num
    self.edge_num_base = edge_num
    self.init_edges(class_number, base_lon, base_lat, self.base, edge_num, cache_size, cache_size_max, cache_size_min)

    self.cst = [[-1 for i in range(class_number + 1)] for j in range(self.edge_num)]
    self.GT_video_number = [{} for i in range(class_number + 1)]

    if os.path.exists("./middle_result") == False:
      os.mkdir("./middle_result")
    if os.path.exists(const.magnet_middle_result.BASE_PATH) == False:
      os.mkdir(const.magnet_middle_result.BASE_PATH)
    if os.path.exists(const.magnet_middle_result.CACHE_HIT_RATIO) == False:
      os.mkdir(const.magnet_middle_result.CACHE_HIT_RATIO)
    if os.path.exists(const.magnet_middle_result.WORKLOAD_BALANCE) == False:
      os.mkdir(const.magnet_middle_result.WORKLOAD_BALANCE)
    if os.path.exists(const.magnet_middle_result.LATENCY) == False:
      os.mkdir(const.magnet_middle_result.LATENCY)
    if os.path.exists(const.magnet_middle_result.TRAFFIC) == False:
      os.mkdir(const.magnet_middle_result.TRAFFIC)
    if os.path.exists(const.magnet_middle_result.CACHE_CLASS) == False:
      os.mkdir(const.magnet_middle_result.CACHE_CLASS)
    if os.path.exists(const.magnet_middle_result.MAG_GROUP) == False:
      os.mkdir(const.magnet_middle_result.MAG_GROUP)
    if os.path.exists(const.magnet_middle_result.MISS_CACHE_CLASS) == False:
      os.mkdir(const.magnet_middle_result.MISS_CACHE_CLASS)


  # update cst table
  def update_cst(self):
    # calculate request number in each class each edge
    class_num = [[0 for i in range(self.class_number + 1)] for j in range(self.edge_num)]
    for edge_id in range(self.edge_num):
      edge_now = self.edge_list[edge_id]
      for vid in list(edge_now.cache.cache.keys()):
        if vid in self.class_dict.keys():
          class_id = self.class_dict[vid]
        else:
          class_id = self.class_number
          continue
        class_num[edge_id][class_id] += 1

    # for each edge, caculate it's best neighbor for each class, according to video number in each class
    for edge_id in range(self.edge_num):
      for vid_class in range(self.class_number + 1):
        vid_class_max = 0
        edge_to = -1
        for neighber_id in self.edge_neighber[edge_id]:
          neighber_num = class_num[neighber_id][vid_class]
          if neighber_num > vid_class_max:
            vid_class_max = neighber_num
            edge_to = neighber_id
        self.cst[edge_id][vid_class] = edge_to

  # Find neighbor edges for each edge.
  def find_neighbors(self):
    for home_edge_id in range(self.edge_num):
      home_edge = self.edge_list[home_edge_id]

      # Scheme 1: select neighbors according to the given neighbor area size
      if const.magnet_params.SELECT_NEIGHBOR_SCHEME == const.magnet_params.SELECT_NEIGHBOR_BY_RADIUS:
        for j in range(self.edge_num):
          if j == i:
            continue
          edge_to = self.edge_list[j]
          if tools.getdis(home_edge.get_lon(), home_edge.get_lat(), edge_to.get_lon(), edge_to.get_lat()) < const.magnet_params.SELECT_RADIUS:
            self.edge_neighber[i].append(j)

      # Scheme 2: select top-k close edges as its neighbors
      if const.magnet_params.SELECT_NEIGHBOR_SCHEME == const.magnet_params.SELECT_NEIGHBOR_BY_NUMBER:
        wait_neighbors = {}
        for edge_to_id in range(self.edge_num):
          if edge_to_id == home_edge_id:
            continue
          edge_to = self.edge_list[edge_to_id]
          wait_neighbors[edge_to_id] = tools.getdis(home_edge.get_lon(), home_edge.get_lat(), edge_to.get_lon(), edge_to.get_lat())
        sorted_wait_neighbors = sorted(wait_neighbors.items(), key=operator.itemgetter(1), reverse=False)
        for i in range(const.magnet_params.NEIGHBOR_NUM):
          self.edge_neighber[home_edge_id].append(sorted_wait_neighbors[i][0])


  # inintial set, including:
  # Initialize the normal edge according to the given parameters.
  # Find neighbor edges for each edge.
  def init_edges(self, class_number, base_lon, base_lat, base, edge_num, cache_size = -1, cache_size_max = 150, cache_size_min = 50):
    # At present, the edges are uniformly distributed
    lon = base_lon
    id = 0
    for i in range(edge_num):
      lat = base_lat
      for j in range(edge_num):
        if cache_size == -1:
          cache_size_now = random.randint(cache_size_min, cache_size_max)
        else:
          cache_size_now = cache_size

        self.edge_list.append(edge.EDGE(class_number, cache_size_now, id, lon, lat))
        self.tot_cache_size += self.edge_list[id].get_size()
        id += 1
        lat += base
      lon += base
    self.find_neighbors()

  # This is because the home edge is busy, 
  # so to find the most free edge in the mag group
  def mag(self, home_edge_id, vid, vid_class):
    mag_now = self.edge_list[home_edge_id].mag
    if mag_now == None:
      edge_to = self.edge_list[home_edge_id]
    else:
      vacancy = 1000
      edge_to = self.edge_list[home_edge_id]
      for neighber in mag_now.edge_list:
        if neighber.vacancy <= vacancy:
          vacancy = neighber.vacancy
          edge_to = neighber
    self.latency += const.default_parameter.LATENCY_NORMAL
    return edge_to.request(vid, vid_class)
    

  # Each edge updates its status
  def change_edge_status(self, tot_req, edge_size):
    for edge_now in self.edge_list:
      edge_now.change_edge_status(tot_req, edge_size)

  # MAG adjustment
  def change_mag(self):
    # Traverse the existing MAG group. 
    # If it is currently idle, delete some idle edges in the group
    del_list = []
    for i in range(len(self.mag_group)):
      mag_now = self.mag_group[i]
      while mag_now.cal_vacancy() <= const.edge_status.BUSY_LINE - 0.2:
        mag_now.del_edge()
      # If the group is less than one edge, 
      # delete it directly. Since there will be problems in deleting the list directly, 
      # record the location first and then delete it
      if mag_now.edge_num <= 1:
        if mag_now.edge_num == 1:
          mag_now.del_edge()
        del_list.append(i)
    for index in reversed(del_list):
      del self.mag_group[index]

    # Traverse all edges. If the edge is a busy edge, 
    # find a suitable free edge from its neighbors to form a mag group, 
    # and add it in turn until the whole group is no longer busy
    for edge_now_id in range(self.edge_num):
      edge_now = self.edge_list[edge_now_id]
      if (edge_now.status == const.edge_status.BUSY and edge_now.mag == None) or (edge_now.mag != None and mag_now.cal_vacancy() > const.edge_status.BUSY_LINE):
        if edge_now.mag == None:
          edge_now.mag = edge.MAG(edge_now)
          self.mag_group.append(edge_now.mag)

        for edge_to_id in self.edge_neighber[edge_now_id]:
          edge_to = self.edge_list[edge_to_id]
          if edge_to.status == const.edge_status.IDLE and edge_to.mag == None:
            edge_to.mag = edge_now.mag
            edge_now.mag.add_edge(edge_to)
            if edge_now.mag.cal_vacancy() <= const.edge_status.BUSY_LINE:
              break

  def print_params(self):
    self.cal_cache_hit_ratio()
    # self.cal_workload_balance()
    # self.cal_latency()
    # self.cal_traffic()
    # self.cal_cache_class()
    # self.print_mag_group()
    self.cache_hit_clear()

  # The function that handles the request
  def request(self, home_edge_id, vid, vid_class, timestamp):
    if home_edge_id < 0:
      home_edge_id = 0
    if home_edge_id >= self.edge_num:
      home_edge_id = self.edge_num - 1
    
    if timestamp >= self.time_p2_end:
        self.time_p2_end += self.time_p2_gap
        self.p2_change_time += 1
        # print("%d times change" % (self.p2_change_time))
        self.update_cst()
        self.change_edge_status(self.tot_requests_count, self.edge_num)
        
    if timestamp >= self.time_p1_end:
        self.time_p1_end += self.time_p1_gap
        self.p1_change_time += 1
        print("%d times change" % (self.p1_change_time))
        self.print_params()
        self.change_mag()
        
    # print(home_edge_id, vid)
    self.tot_requests_count += 1
    if vid not in self.GT_video_number[vid_class].keys():
      self.GT_video_number[vid_class][vid] = 0
    self.GT_video_number[vid_class][vid] += 1

    # home edge and 
    home_edge = self.edge_list[home_edge_id]

    ratio_hit_home = False
    ratio_hit_neighber = False
    
    # If the edge is busy, 
    # it will find the most idle edge sharing request from its MAG group
    ratio_hit_home = home_edge.request(vid, vid_class)

    if ratio_hit_home == False and vid_class != self.class_number:
      # neighbor edge
      neighber_id = self.cst[home_edge_id][vid_class]
      neighber_to = self.edge_list[neighber_id]
      ratio_hit_neighber = neighber_to.request(vid, vid_class)
      self.latency += tools.getdis(home_edge.get_lon(), home_edge.get_lat(), neighber_to.get_lon(), neighber_to.get_lat())

    if ratio_hit_home == True:
      self.home_edge_hit_count += 1
    elif ratio_hit_neighber == True:
      self.home_edge_hit_count += 1
      self.traffic += const.default_parameter.TRAFFIC_NORMAL
    else:
      self.traffic += const.default_parameter.TRAFFIC_CLOUD
      self.latency += const.default_parameter.LATENCY_CLOUD

    hit_home_neighber = (ratio_hit_home or ratio_hit_neighber)

    if hit_home_neighber == False:
      self.cache_miss_class[vid_class] += 1

    return hit_home_neighber

  def cal_latency(self):
    file_name = const.magnet_middle_result.LATENCY + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    write_data = [self.latency]
    csv_writer.writerow(write_data)
    f.close()
    self.latency = 0


  def cal_traffic(self):
    file_name = const.magnet_middle_result.TRAFFIC + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    write_data = [self.traffic]
    csv_writer.writerow(write_data)
    f.close()
    self.traffic = 0

  def cal_workload_balance(self):
    # print("================================")
    # print("cache workload balance")
    # print("================================")
    request_count = []
    cache_size = []

    for edge in self.edge_list:
      num_now = edge.req_num
      if edge.mag != None:
        num_now += edge.mag.req_num // edge.mag.edge_num
      request_count.append(num_now)
      cache_size.append(edge.get_size())
    
    # min-max normalize
    R_max = max(request_count) + 5
    R_min = min(request_count) - 5
    norm_request_count = [(x - R_min) / (R_max - R_min + 1) * 100 for x in request_count]

    avg_size = self.tot_cache_size / self.edge_num

    for i in range(len(norm_request_count)):
      norm_request_count[i] = norm_request_count[i] / (cache_size[i] / avg_size)
    avg_norm_req = np.mean(norm_request_count)

    WSD = math.sqrt(sum([(x - avg_norm_req) ** 2 for x in norm_request_count]))

    # for i in range(len(norm_request_count)):
    #   print(i, norm_request_count[i])
    # print("avg : %.5f , WSD : %.5f" % (avg_norm_req, WSD))

    file_name = const.magnet_middle_result.WORKLOAD_BALANCE + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    write_data = [WSD]
    csv_writer.writerow(write_data)
    f.close()

  # according to lon and lat, find the home edge
  def get_home_edge(self, lon, lat):
    i = (lon - self.base_lon) // self.base
    j = (lat - self.base_lat) // self.base
    main_id = int(i * self.edge_num_base + j)
    if main_id >= self.edge_num:
      main_id = self.edge_num - 1
    elif main_id <= 0:
      main_id = 0
    return main_id

  def print_mag_group(self):
    file_name = const.magnet_middle_result.MAG_GROUP + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    for mag in self.mag_group:
      write_data = []
      for edge in mag.edge_list:
        write_data.append(edge.get_id())
      csv_writer.writerow(write_data)
    f.close()

  def cache_hit_clear(self):
    self.home_edge_hit_count = 0
    self.tot_requests_count = 0

  # Calculate the top 3 GT with the largest proportion of cache in each edge,
  # and write into log file
  def cal_cache_class(self):
    file_name = const.magnet_middle_result.CACHE_CLASS + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    csv_writer.writerow(["edge id", "total proportion","GT-A", "proportion", "GT-B", "proportion", "GT-C", "proportion"])
    for edge_id in range(0, self.edge_num):
      edge_now = self.edge_list[edge_id]
      # if len(edge_now.cache.cache) <= 0.2 * edge_now.cache.capacity:
      #   continue
        
      write_data = [edge_id, len(edge_now.cache.cache)]
      if edge_now.cache.capacity == 0:
        csv_writer.writerow(write_data)
        continue

      class_num = {}
      for vid in list(edge_now.cache.cache.keys()):
        if vid in self.class_dict.keys():
          class_id = self.class_dict[vid]
        else:
          class_id = self.class_number
          continue
        if class_id not in class_num.keys():
          class_num[class_id] = 0
        class_num[class_id] += 1
      
      sorted_class_num = sorted(class_num.items(), key=operator.itemgetter(1), reverse=True)

      num = 0
      for i in sorted_class_num:
        if num >= 3:
          break
        num += 1
        pro = i[1] * 100 / edge_now.cache.capacity
        if pro >= 10:
          write_data.append(i[0])
          write_data.append(pro)
      
      csv_writer.writerow(write_data)
    f.close()


  def cal_cache_hit_ratio(self):
    # print("================================")
    # print("cache hit ratio")
    # print("================================")
    if self.tot_requests_count == 0:
      cache_hit_ratio = 0
    else:
      cache_hit_ratio = self.home_edge_hit_count / self.tot_requests_count * 100
    # print("home_edge_hit_count = %d , tot_requests_count = %d" % (self.home_edge_hit_count, self.tot_requests_count))
    print("edge cache hit ratio = %.5f%%" % (cache_hit_ratio))

    file_name = const.magnet_middle_result.CACHE_HIT_RATIO + str(self.p1_change_time) + ".csv"
    f = open(file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    write_data = [cache_hit_ratio]
    csv_writer.writerow(write_data)
    f.close()


