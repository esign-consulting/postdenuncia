#!/usr/bin/env python3

import pandas as pd
import folium
import os
import time
from selenium import webdriver

csv_input = 'fumaca-preta/denuncias-sao-paulo-sp.csv'
data = pd.read_csv(csv_input)
data['latitude'] = data['latitude'].str.replace(',', '.').astype(float)
data['longitude'] = data['longitude'].str.replace(',', '.').astype(float)

center = [data['latitude'].mean(), data['longitude'].mean()]
map = folium.Map(location = center, zoom_start = 12)
for index, row in data.iterrows():
    location = [row['latitude'], row['longitude']]
    folium.Marker(location).add_to(map)

html_output = 'fumaca-preta/mapa-denuncias-sao-paulo-sp.html'
map.save(html_output)

browser = webdriver.Firefox()
browser.get('file://{path}/{mapfile}'.format(path = os.getcwd(), mapfile = html_output))
time.sleep(5)
png_output = 'fumaca-preta/mapa-denuncias-sao-paulo-sp.png'
browser.save_screenshot(png_output)
browser.quit()
