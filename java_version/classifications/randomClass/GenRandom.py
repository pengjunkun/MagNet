import csv
import os
import random


class GenRandom:
    def __init__(self, size):
        self.size = size

    def run(self):
        print("random,size={}".format(self.size))
        random_list = random.choices(range(0, self.size, 1), k=39765)

        index=0
        with open('./vectors/seqGT100.vec') as vector_file:
            with open('./randomClass/typeResult/nodes{}.csv'.format(self.size), 'w') as save_file:
                f_csv = csv.reader(vector_file, delimiter=' ')
                for node in f_csv:
                    save_file.write(node[0] + ":" + str(random_list[index]))
                    save_file.write("\n")
                    index+=1
