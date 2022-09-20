import tools.classifier as classifier, tools.tools as tools
import tools.const as const
import pandas as pd
import os, csv

class DATA_CONTROLLER():
  def __init__(self, kwargs, input_file_name, class_number, base_lon, base_lat, gap):
    
    self.base_lon = base_lon
    self.base_lat = base_lat
    self.gap = gap
    self.class_number = class_number

    # chosen_file_name = input_file_name[:-4]+"_"+str(base_lon)+"_"+str(base_lat)+"_"+str(gap)+"_"+str(const.default_parameter.NROWS)+".csv"
    classified_file_name = input_file_name[:-4]+"_"+str(self.class_number)
    # print(chosen_file_name)
    if os.path.exists(input_file_name) == False:
      print("================================")
      print("filter file '%s' does not exist" % (input_file_name))
      print("================================")
    else:
      print("================================")
      print("filter file '%s' exist" % (input_file_name))
      print("================================")
    
    self.input_file_name = input_file_name
    
    # classify videos
    cls = classifier.CLASSIFIER()
    if kwargs['classifier'] == 'random':
      self.class_dic = cls.random_classifier(self.input_file_name, classified_file_name, 
        self.class_number)
    elif kwargs['classifier'] == 'furthest':
      self.class_dic = cls.furthest_classifier(self.input_file_name, classified_file_name, 
        self.class_number)
  

  """
  Read files and simulate video requests in sequence
  """
  def run(self, edge_control):
    self.edge_control = edge_control

    reader = pd.read_csv(self.input_file_name ,header=None, iterator=True, 
      chunksize=const.default_parameter.CHUNK_SIZE, nrows=const.default_parameter.NROWS)

    tot_data_len = 0
    missed_num = 0
    for chunk in reader:
      tot_data_len += len(chunk)
    
      for i in range(len(chunk)):
        data_now = chunk.iloc[i]
        lon_now = data_now[const.data_list_name.LON]
        lat_now = data_now[const.data_list_name.LAT]
        time_now = int(data_now[const.data_list_name.TIMESTAMP])
        vid_real = int(data_now[const.data_list_name.VID])
        
        # Find the category corresponding to the video according to the video classification dictionary
        if vid_real not in self.class_dic:
          vid_class = self.class_number
          missed_num += 1
        else:
          vid_class = self.class_dic[vid_real]

        # Calculate the current requested home edge according to the location information
        home_edge_id = edge_control.get_home_edge(lon_now, lat_now)
        # edge_control.request(home_edge_id, vid_class, time_now)
        hit_ratio = edge_control.request(home_edge_id, vid_real, vid_class, time_now)
        # print(vid_real, vid_class, hit_ratio)

    print("work done, totally %d items" % (tot_data_len))
    print("%d items missed" % (missed_num))
    # edge_control.save()


