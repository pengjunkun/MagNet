EPS = 1e-3

class default_parameter():
  TRAFFIC_CLOUD = 5
  TRAFFIC_NORMAL = 1
  TRAFFIC_CLOUD_IP = 5
  TRAFFIC_NORMAL_IP = 1


  LATENCY_CLOUD = 30
  LATENCY_NORMAL = 1
  LATENCY_CLOUD_IP = 30
  LATENCY_NORMAL_IP = 1

  FILE_NAME = "./data/datagen.csv"
  FILE_NAME_IP = "./data/ip_data_solve.csv"

  CLASSIFIED_CHUNK_SIZE = 10000
  CHUNK_SIZE = 400
  NROWS = None

  EDGE_NUM = 20
  CACHE_SIZE = 135

  CLASS_NUMBER = 621
  BASE_LON = 116
  BASE_LAT = 39.7
  GAP = 0.2

  beg_time = 1430668800
  TIME_P1_GAP = 1800
  TIME_P1_END = beg_time
  TIME_P2_GAP = 60
  TIME_P2_END = beg_time

  TIME_P1_GAP_IP = 1800
  TIME_P1_END_IP = beg_time
  TIME_P2_GAP_IP = 60
  TIME_P2_END_IP = beg_time
  
  P1_P2 = TIME_P1_GAP // TIME_P2_GAP


class magnet_middle_result():
  BASE_PATH = "./middle_result/magnet_middle_result/"
  CACHE_HIT_RATIO = "./middle_result/magnet_middle_result/cache_hit_ratio/"
  WORKLOAD_BALANCE = "./middle_result/magnet_middle_result/workload_balance/"
  LATENCY = "./middle_result/magnet_middle_result/latency/"
  TRAFFIC = "./middle_result/magnet_middle_result/traffic/"
  CACHE_CLASS = "./middle_result/magnet_middle_result/cache_class/"
  MAG_GROUP = "./middle_result/magnet_middle_result/mag_group/"
  MISS_CACHE_CLASS = "./middle_result/magnet_middle_result/miss_cache_class/"

class data_list_name():
  UID = 0
  TIMESTAMP = 1
  LON = 2
  LAT = 3
  VID = 4

class data_list_name_ip():
  TIMESTAMP = 0
  VID = 1
  IP = 2
  HOME_EDGE = 3

class edge_status():
  BUSY_LINE = 1.5
  NORMAL_LINE = 0.6
  BUSY = 2
  NORMAL = 1
  IDLE = 0

class magnet_params():
  SELECT_RADIUS = 7
  SELECT_RADIUS_IP = 4
  NEIGHBOR_NUM = 154

  SELECT_NEIGHBOR_BY_NUMBER = 0
  SELECT_NEIGHBOR_BY_RADIUS = 1
  SELECT_NEIGHBOR_SCHEME = SELECT_NEIGHBOR_BY_NUMBER

  QUICK_CACHE_CLEAR_BY_GT = 0
  QUICK_CACHE_CLEAR_BY_VIDEO = 1
  QUICK_CACHE_CLEAR_SCHEME = QUICK_CACHE_CLEAR_BY_VIDEO

  QUICK_CACHE_MAX_GT_NUMBER = 3
  QUICK_CACHE_CLEAN_GT_NUMBER = 6
  QUICK_CACHE_CLEAN_VIDEO_NUMBER = 10
  QUICK_CACHE_CLEAN_GT_PRO_BASE = 0.03

