import csv
import os
import numpy as np
import scipy.spatial.distance


class Classify:
    def __init__(self,size):
        self.size=size
        self.centers=np.loadtxt("./KMeans/typeResult/KMeansCenters{}.csv".format(size))

    def run(self):
        with open('./vectors/seqGT100.vec') as f:
            with open('./KMeans/typeResult/nodes{}.csv'.format(self.size),'w') as f2:
                f_csv=csv.reader(f,delimiter=' ')
                for node in f_csv:
                    fea=node[1:]
                    result=self.classRes(fea)
                    f2.write(node[0]+":"+str(result))
                    f2.write("\n")

    def classRes(self,features):
        tmp= scipy.spatial.distance.cdist(np.reshape(features,(1,-1)),self.centers)
        res=np.argmin(tmp)
        return res

    # def doClassify(self):




if __name__=='__main__':
    files=os.listdir("./centersResult/")
    for file in files:
        size=file.lstrip('KMeansCenters')
        size=size.rstrip('.csv')
        classify=Classify(size)
        classify.run()
        print("finish:{}".format(size))
