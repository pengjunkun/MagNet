import edge_controller.magnet_edge_controller as magnet_edge_controller
import tools.data_controller as data_controller
import tools.const as const
import argparse


def main(kwargs):
  # some default settings, you can change them in ./tools/const.py
  input_file_name = const.default_parameter.FILE_NAME
  class_number = const.default_parameter.CLASS_NUMBER

  base_lon = const.default_parameter.BASE_LON
  base_lat = const.default_parameter.BASE_LAT
  gap = const.default_parameter.GAP

  cache_size = const.default_parameter.CACHE_SIZE
  edge_num = const.default_parameter.EDGE_NUM

  data_control = data_controller.DATA_CONTROLLER(kwargs=kwargs, input_file_name=input_file_name, class_number=class_number,
    base_lon=base_lon, base_lat=base_lat, gap=gap)

  # according to different model parameters, choose edge controller

  edge_control = magnet_edge_controller.MAGNET_EDGE_CONTROLLER(class_number=class_number, edge_num=edge_num,
      base_lon=base_lon, base_lat=base_lat, gap=gap, cache_size=cache_size, class_dict=data_control.class_dic)
  
  # run data controller
  data_control.run(edge_control)


if __name__ == "__main__":
  parser = argparse.ArgumentParser()

  # choose how to classify videos into different class
  parser.add_argument('--classifier', default='random', help='random | furthest')

  args = parser.parse_args()
  print(args)
  kwargs = vars(args)

  main(kwargs)

