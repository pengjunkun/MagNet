import csv
import os
import random

from scipy.spatial.distance import cdist


class GenFurthest:
    def __init__(self, type_number):
        print("Furthest,{}start".format(type_number))
        self.type_number = type_number
        self.X = []
        # with open('./vectors/test.vec') as f:
        with open('./vectors/seqGT100.vec') as f:
            f_csv = csv.reader(f, delimiter=' ')
            for row in f_csv:
                self.X.append(row)

        self.total_node_number=len(self.X)
        # caution: not all typea have a same size
        self.one_type_size = int(self.total_node_number / self.type_number)
        self.size_index_threshold = self.total_node_number % self.type_number
        print("data ready")

    def run(self):
        if not os.path.exists("./furthestClass/typeResult"):
            os.makedirs("./furthestClass/typeResult")

        with open('./furthestClass/typeResult/nodes{}.csv'.format(self.type_number), 'w') as f2:
            typeId = 0
            while (len(self.X) > self.one_type_size):
                ids = self.get_one_type_list(typeId)
                for id in ids:
                    f2.write(str(id) + ":" + str(typeId))
                    f2.write("\n")
                typeId += 1
            # save the rest of nodes
            for node in self.X:
                f2.write(str(node[0]) + ":" + str(typeId))
                f2.write("\n")

    def get_one_type_list(self,typeId):
        # caution: id and index are two things
        current_type_size=self.one_type_size
        if typeId<self.size_index_threshold:
            current_type_size+=1

        new_type_ids = []
        new_type_nodes = []
        # get a x randomly, and remove itself
        x_index = random.randrange(len(self.X))
        new_type_ids.append(self.X[x_index][0])
        new_type_nodes.append(self.X[x_index][1:])
        self.X.remove(self.X[x_index])

        # initially, there is x
        for i in range(current_type_size-1):
            index = self.get_top(new_type_nodes)
            new_type_ids.append(self.X[index][0])
            new_type_nodes.append(self.X[index][1:])
            self.X.remove(self.X[index])

        return new_type_ids

    def get_top(self, current_nodes):
        # if len(current_nodes)>50:
        #     tmp_nodes=random.choices(current_nodes,k=50)
        #     distances = cdist([one[1:] for one in self.X], tmp_nodes, 'euclidean')
        # else:

        # todo
        # this can be optimized by record all distance firstly
        distances = cdist([one[1:] for one in self.X], current_nodes, 'euclidean')
        distances = distances.sum(axis=1)
        distances = distances.tolist()
        return distances.index(max(distances))
