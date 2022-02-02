import numpy as np
from sklearn.cluster import KMeans
from scipy.spatial.distance import cdist
import csv

from KMeans.classify import Classify


class KMCluster:
    def __init__(self,k):
        self.mm=None
        self.prec=None
        self.k=k
        self.X=[]
        with open('./vectors/seqGT100.vec') as f:
            f_csv=csv.reader(f,delimiter=' ')

            for row in f_csv:
                self.X.append(row[1:])
        print("data ready")


    def runKM(self):
        print("KMeans,start："+str(self.k))
        kmeans=KMeans(n_clusters=self.k)
        kmeans.fit(self.X)
        self.mm=kmeans.cluster_centers_
        print("finish: k="+str(self.k))
        diff=cdist(self.X,self.mm,'euclidean')
        ndiff=np.min(diff,axis=1)
        self.prec=sum(ndiff)/len(self.X)
        self.writeToFile()
        Classify(self.k).run()

    def result(self):
        return self.k,self.prec,self.mm

    def writeToFile(self):
        k,prec,mm=self.result()
        with open("./KMeans/typeResult/KMeansCenters{}.csv".format(k),"w") as f:
            for line in list(mm):
                f.write(" ".join([str(x) for x in line.tolist()]))
                f.write("\n")



