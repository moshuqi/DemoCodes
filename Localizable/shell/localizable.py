# -*- coding: UTF-8 -*-
import os
import time

def findAllFile(path):
	files = os.listdir(path)
	# 找出多语言文件夹
	dirs = []	
	for file in files:
		# 包含多语言的文件夹名称以.lproj结尾
		if ".lproj" in file:
			dirs.append(file)
	
	# 找出多语言文件
	files = []
	for dir in dirs:
		# 多语言文件名为 Localizable.strings
		fp = os.path.join(path, dir, "Localizable.strings")
		if os.path.exists(fp):
			files.append(fp)

	return files

def getDict(file):
	# 读取多语言文件内容，并保存成一个字典
	d = {}
	# 打开文件，逐行对去文本，暂时只考虑每条多语言只占用一行
	f = open(file, "r")
	lines = f.readlines()
	for line in lines:
		# 判断是否为一条多语言
		if ";" in line:
			strs = line.split("=")
			if len(strs) != 2:
				continue

			# 去除字符串首尾空格
			key = strs[0]
			key = key.strip()
			value = strs[1]
			value = value.strip()
			d[key] = value	

	f.close()
	return d

def createEntireContent(files):
	# 从所有文件中找出完整的多语言键值序列
	content = {}
	for file in files:
		d = getDict(file)
		content.update(d)	

	return content

def writeContent(content, file):
	# 将不存在于file中的多语言内容写入到文件中
	newContent = {}
	d = getDict(file)
	for key in content.keys():
		if d.has_key(key) == False:
			newContent[key] = content[key]

	# 没有新内容
	if len(newContent.keys()) == 0:
		return

	# print("Some different: ", file)
	# print(newContent)

	# 需要新写入的文本内容
	timeStr = time.strftime('%Y-%m-%d-%H:%M:%S', time.localtime(time.time()))
	string = "\n\n# ################### Add by script at " + timeStr + " ###############\n" 
	for key in newContent.keys():
		line = key + " = " + newContent[key] + "\n"
		string += line		

	# 写入到文件已有内容之后
	f = open(file, "a")
	f.write(string)			
	f.close()

	print(file)
	print("Add new content: \n" + string)

def process():
	print("process ...")
	path = "./Localizable"
	files = findAllFile(path)

	print("Localizable files: ")
	print(files)

	print("createEntireContent ... ")
	contentDict = createEntireContent(files)
	# print(contentDict)

	for file in files:
		writeContent(contentDict, file)

process()




