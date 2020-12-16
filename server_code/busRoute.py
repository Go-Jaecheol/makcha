from bs4 import BeautifulSoup
from requests.packages.urllib3.exceptions import InsecureRequestWarning
import requests
import time

busList = []
#busN = "937"#input("원하는 버스 번호: ")
#list.append(busN)
requests.packages.urllib3.disable_warnings(InsecureRequestWarning)
url = "https://businfo.daegu.go.kr:8095/dbms_web/"
print(url)
r = requests.get(url, verify=False)
soup = BeautifulSoup(r.text, "lxml")
body = soup.find("body")

jumbo = body.find_all("div", "justify-content-center link")
for a in jumbo:
    a_tag = a.find("a")
    if "href" in str(a_tag):  # a태그 안에 href속성이 존재하는지 확인
        str = a_tag["href"]
        print(str)
        new_url = str[10:]
        url = url + new_url
print(url)

from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import Select
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

driver = webdriver.Chrome('C:/Users/user/AppData/Local/Programs/Python/Python38/Scripts/chromedriver.exe')
driver.implicitly_wait(10)
driver.get(url)

req = driver.page_source
soup = BeautifulSoup(req, 'lxml')
busName = soup.find_all("option")

busNameList = []
busIndex = 0
for i in busName:
    if len(i.text) < 6:
        busNameList.append([])
        busNameList[busIndex].append(i.text)
        busIndex += 1
del busNameList[0:2]
#print(busNameList)

for i in range(0, busIndex-2):
    for busN in busNameList[i]:
        #print(busN)
        stopList = []
        stopList.append(busN)
        routebox = Select(driver.find_element_by_id('routeNo'))
        routebox.select_by_visible_text(busN)
        
        time.sleep(10)

        req = driver.page_source
        soup = BeautifulSoup(req, 'lxml')
        numRoute = soup.select('.businfoBtn')
        if not numRoute:
            break

        #경로 여러개 인 경우
        #for i in numRoute:
            #if i.find("span").text == '경로보기':
                #print(i)

        seeroute = driver.find_element_by_class_name('businfoBtn').send_keys(Keys.ENTER)
        try:
            element = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.CLASS_NAME, "scheduleList"))
            )
        finally:
            req = driver.page_source

        soup = BeautifulSoup(req, 'lxml')
        busgap = soup.find_all("section")
        cnt = 0
        for i in busgap:
            hidden = i.find("tr", "scheduleList")
            tr = hidden.find("tbody")

        reverseCheck = 0
        for i in tr:
            if cnt%2 == 1:
                td = i.findAll("td")
                if reverseCheck%2 == 1:
                    for s in td[::-1]:   #역순 출력
                        if s.text is not s.text.strip():
                            stopList.append(s.text)
                            #print(s.text)
                else:
                    for s in td:
                        if s.text is not s.text.strip():
                            stopList.append(s.text)
                            #print(s.text)
                reverseCheck += 1
            cnt += 1
    print(stopList)
    busList.append(stopList)   #버스 number와 경로가 담기는 최종 list (2차원 배열)
driver.close()

#problem :
# 1. 운행시간표보기 버튼과 경로보기 버튼 구별 불가.
# 2. 경로가 여러개 있을 경우 하나만 출력됨. (1번이 해결 도면 2번도 해결할 수 있을 것으로 예상)
# 3. 버스 번호를 변경이 되기 전에 경로를 읽어버려서 time.sleep(10)으로 해결. but, 많은 시간이 소요됨.
#    WebDriverWait...until(EC.presence_of_element_located...) 을 사용하려 했지만 EC.presence_of_element_located
#    의 인자에 무엇을 넣어야 할지 찾지 못함.


