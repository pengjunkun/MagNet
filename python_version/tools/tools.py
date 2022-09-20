import math
import tools.const

EARTH_REDIUS = 6378.137
def rad(d):
    return d * math.pi / 180.0

# Calculate actual distance using latitude and longitude
def getdis(lon1, lat1, lon2, lat2):
    radLat1 = rad(lat1)
    radLat2 = rad(lat2)
    a = radLat1 - radLat2
    b = rad(lon1) - rad(lon2)
    s = 2 * math.asin(math.sqrt(math.pow(math.sin(a/2), 2) + math.cos(radLat1) * math.cos(radLat2) * math.pow(math.sin(b/2), 2)))
    s = s * EARTH_REDIUS
    return s

def getdis_ip(ip1, ip2):
    num1 = ip1.split('.')
    num2 = ip2.split('.')
    for i in range(4):
        if num1[i]!=num2[i]:
            return 4 - i
    return 0

def pearson(vector1, vector2):
    n = len(vector1)
    #simple sums
    sum1 = sum(float(vector1[i]) for i in range(n))
    sum2 = sum(float(vector2[i]) for i in range(n))
    #sum up the squares
    sum1_pow = sum([v**2 for v in vector1])
    sum2_pow = sum([v**2 for v in vector2])
    #sum up the products
    p_sum = sum([vector1[i]*vector2[i] for i in range(n)])

    num = p_sum - (sum1*sum2/n)
    den = math.sqrt((sum1_pow-sum1**2/n)*(sum2_pow-sum2**2/n))
    if den == 0:
        return 0.0
    return num / den

