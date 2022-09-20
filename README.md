This repository is created for the project MagNet, whose corresponding paper can be found in "https://doi.org/10.1145/3485447.3512146".



There are two versions of MagNet: Java and Python (suggested).

# Running Instructions (python version)

#### quick start

As the dataset used in the paper is not opensourced, we provide a method to generatea test dataset for readers to understand our solution.

In practice, readers should organize their own real datasets.

1. Use **datagen.ipynb** to generate **datagen.csv**, Set some parameters in **./tools/const** as follows：
- default_parameter.FILE_NAME = datagen.csv
- BASE_LON = 116
- BASE_LAT = 39.7
- GAP = 0.2
- beg_time = 1430668800

In the generated data, each trace consists of [uid, time_stamp, lon, lat, vid], whichstands for [user_id, timestamp, longitude, latitude, video_id].

2. Run the following code:

`python3 main.py --classifier=random`

---

#### params

`python3 main.py --help`

- --classifier, in order to classify videos into several class (random | cf | furthest).

---

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

# Paper Details

Junkun Peng, Qing Li, Xiaoteng Ma, Yong Jiang, Yutao Dong, Chuang Hu,
and Meng Chen. 2022. MagNet: Cooperative Edge Caching by Automatic
Content Congregating. In Proceedings of the ACM Web Conference 2022
(WWW ’22), April 25–29, 2022, Virtual Event, Lyon, France. ACM, New York,
NY, USA, 10 pages. https://doi.org/10.1145/3485447.351214``

# Contacts

If you have any problem about this paper/repository or want to discuss the topic with us, you are welcomed to send emails to pengjunkun@gmail.com

Thank you!
