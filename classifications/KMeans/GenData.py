import torch


class GenData:
    def gen(self,w, b, num_examples):  #@save
        """生成 y = Xw + b + 噪声。"""
        X = torch.normal(0, 1, (num_examples, len(w)))
        y = torch.matmul(X, w) + b
        y += torch.normal(0, 0.01, y.shape)
        return X, y.reshape((-1, 1))


if __name__ == "__main__":
    genData=GenData()
    features,labels=genData.gen(torch.tensor([1.0,2.]),5.0,100)
    print(features)