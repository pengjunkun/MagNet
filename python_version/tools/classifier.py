import tools.const as const
import random, numpy as np, pandas as pd, os, csv

class CLASSIFIER():
  def real_random_classifier(self, input_file_name, classified_file_name, class_number = 1000):
    reader = pd.read_csv(input_file_name ,header=None, iterator=True, 
        chunksize=const.default_parameter.CLASSIFIED_CHUNK_SIZE, nrows=const.default_parameter.NROWS)
    class_dic = {}
    tot_data_len = 0
    for chunk in reader:
      tot_data_len += len(chunk)
      for i in range(len(chunk)):
        data_now = chunk.iloc[i]
        vid_now = int(data_now[const.data_list_name.VID])
        if vid_now not in class_dic:
          class_now = random.randint(0,class_number-1)
          class_dic[vid_now] = class_now
      if tot_data_len % 100000 == 0:
        print("%d data has already classified" % (tot_data_len))
    print("classfied done, totally %d items" % (tot_data_len))
      
    f = open(classified_file_name, 'w', newline='')
    csv_writer = csv.writer(f)
    write_data = []
    for key in class_dic.keys():
      write_data = [key, class_dic[key]]
      csv_writer.writerow(write_data)
    f.close()

    return class_dic

  # random classifier, already realized in self.real_random_classifier
  def random_classifier(self, input_file_name, classified_file_name, class_number = 1000):
    classified_file_name = classified_file_name + "_random_classifier.csv"
    if os.path.exists(classified_file_name) == False:
      print("================================")
      print("random classified file '%s' does not exist" % (classified_file_name))
      print("================================")
      return self.real_random_classifier(input_file_name, classified_file_name, class_number)
      
    print("================================")
    print("read random classified file '%s'" % (classified_file_name))
    print("================================")

    class_dic = {}
    tot_data_len = 0
    reader = pd.read_csv(classified_file_name ,header=None, iterator=True, 
        chunksize=const.default_parameter.CLASSIFIED_CHUNK_SIZE, nrows=const.default_parameter.NROWS)
    for chunk in reader:
      tot_data_len += len(chunk)
      for i in range(len(chunk)):
        data_now = chunk.iloc[i]
        vid_now = int(data_now[0])
        class_id = int(data_now[1])
        class_dic[vid_now] = class_id
      if tot_data_len % 100000 == 0:
        print("%d data has already classified" % (tot_data_len))
    print("classfied file read done, totally %d items" % (tot_data_len))
    
    return class_dic


  # furthest_classifier, not realized
  def furthest_classifier(self, input_file_name, classified_file_name, class_number = 1000):
    # classified_file_name = classified_file_name + "_cf_classifier.csv"
    classified_file_name = const.default_parameter.FILE_NAME[:-4] + '_'+str(class_number)+'_furthest.csv'
    if os.path.exists(classified_file_name) == False:
        print("================================")
        print("error : cf classified file '%s' does not exist" % (classified_file_name))
        print("================================")
        exit(-1)

    print("================================")
    print("read cf classified file '%s'" % (classified_file_name))
    print("================================")

    class_dic = {}
    tot_data_len = 0
    reader = pd.read_csv(classified_file_name ,header=None, iterator=True, 
        chunksize=const.default_parameter.CLASSIFIED_CHUNK_SIZE, nrows=const.default_parameter.NROWS)
    for chunk in reader:
      tot_data_len += len(chunk)
      for i in range(len(chunk)):
        data_now = chunk.iloc[i]
        vid_now = int(data_now[0])
        class_id = int(data_now[1])
        class_dic[vid_now] = class_id
      if tot_data_len % 100000 == 0:
        print("%d data has already classified" % (tot_data_len))
    print("classfied file read done, totally %d items" % (tot_data_len))
    
    return class_dic