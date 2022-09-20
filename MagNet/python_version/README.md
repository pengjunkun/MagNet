#magnet

****

#### quick start

As the dataset used in the paper is not opensourced, we provide a method to generatea test dataset for readers to understand our solution. 

In practice, readers should organize their own real datasets.

1. Use **datagen.ipynb** to generate **datagen.csv**, Set some parameters in **./tools/const** as follows：

- default_parameter.FILE_NAME = datagen.csv
- BASE_LON = 116
- BASE_LAT = 39.7
- GAP = 0.2
- beg_time = 1430668800

In the generated data, each trace consists of [uid, time_stamp, lon, lat, vid], whichstands for [user_id, timestamp, longitude, latitude, video_id].

2. Run the following code:

``` python
python3 main.py --classifier=random
```

****

#### params

``` python
python3 main.py --help
```

- --classifier, in order to classify videos into several class (random | cf | furthest).

****

#### file structure  

- data: Including video request data and video classification data.
- middle_result: Include intermediate generated data to support subsequent data visualization.
- edge_controller: Contains the video request processing function, which is the main function of magnet.
- tools: Contains various tool functions, such as LRU functions and edge classes.
  - const.py: It contains various parameters, including the file name of the video request data, the number of edges, the edge cache capacity, the longitude and latitude information of the edge distribution, etc.
- datagen.ipynb: Used to generate video request data.
- main.py: main function.

#### Operation Result

- Follow the default_parameter.TIME_P1_GAP in const.py as the interval. Every time, the cache hit rate and other information will be printed and stored in the middle_result.
- You can call the code in draw.ipynb to print the change curve of cache hit rate.
