import csv
import heapq


class HotBalance:
    def __init__(self):
        pass

    def get_vid_dict(self):
        vid_counter_dict = {}
        # with open("../../data/iqiyi/1h.csv", 'r') as file:
        with open("../../data/iqiyi/12days.csv", 'r') as file:
            # with open("../data/iqiyi/12days.csv", 'r') as file:
            # with open("../vectors/oneDay.csv", 'r') as file:
            csv_reader = csv.reader(file)
            for trace in csv_reader:
                if len(trace) < 5:
                    continue
                vid = int(trace[4])
                vid_counter_dict.update({"{}".format(vid): vid_counter_dict.get("{}".format(vid), 0) + 1})
        print("in this dataset, it has {} vids".format(len(vid_counter_dict)))
        return vid_counter_dict

    def huffmanClassify(self, result_dict, GTNs, threshold):
        # in vids_heap, (freq,vids)
        vids_heap = [(x[1], x[0]) for x in result_dict.items()]
        heapq.heapify(vids_heap)
        # remove vids whose freq of 12days < threshold
        min_vid = heapq.heappop(vids_heap)
        while min_vid[0] < threshold:
            min_vid = heapq.heappop(vids_heap)
        heapq.heappush(vids_heap, min_vid)
        print("after reduce,has {} vids".format(len(vids_heap)))

        count = 0
        while len(vids_heap) > min(GTNs):
            count += 1
            # pop will remove and return the last one in this heap
            A = heapq.heappop(vids_heap)
            B = heapq.heappop(vids_heap)
            new_vids = "{},{}".format(A[1], B[1])
            new_freq = (A[0] + B[0])
            heapq.heappush(vids_heap, (new_freq, new_vids))

            if len(vids_heap) in GTNs:
                self.write_to_file(vids_heap, threshold)

            if count % 1000 == 0:
                print(count)

    def write_to_file(self, vids_heap, threshold):
        GTN = len(vids_heap)
        GT = 0
        with open("./typeResult/nodes{}.csv".format(GTN), 'w') as file:
            # with open("./typeResult/nodes{}_{}.csv".format(GTN, threshold), 'w') as file:
            for pair in vids_heap:
                vids = pair[1].split(',')
                for vid in vids:
                    file.write("{}:{}\n".format(vid, GT))
                GT += 1
        print("finished:{}".format(GTN))


if __name__ == '__main__':
    hotBalance = HotBalance()
    result_dict = hotBalance.get_vid_dict()
    # as after reduce, len(vid_heap)<39000, we should use threshold=50 for this scenario
    GTNs = set([38, 77, 155, 310, 621, 1242, 2485, 4970, 9941, 19882, 39000])
    # GTNs = set([621])

    # thresholds = [1,5,10, 50, 100, 300, 1000]
    thresholds = [100]
    for threshold in thresholds:
        hotBalance.huffmanClassify(result_dict, GTNs, threshold)
