
from collections import OrderedDict


class LRUCache(OrderedDict):
  def __init__(self, capacity):
    self.capacity = capacity
    self.cache = OrderedDict()

  def change_capacity(self, capacity):
    self.capacity += capacity

  def get_capacity(self):
    return self.capacity

  def is_full(self):
    return len(self.cache) >= self.capacity

  def is_in(self, key):
    if self.capacity == 0:
      return False
    if key in self.cache:
      value = self.cache.pop(key)
      self.cache[key] = value
      return True
    else:
      return False

  def get(self, key):
    if self.capacity == 0:
      return False
    if key in self.cache:
      value = self.cache.pop(key)
      self.cache[key] = value
      return True
    else:
      while len(self.cache) >= self.capacity:
        self.cache.popitem(last=False)  # pop出第一个item
      self.cache[key] = True
      return False

