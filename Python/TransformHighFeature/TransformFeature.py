from sys import argv

def read_dataset(File_Name, Split_Symbol):
	f = open(File_Name)
	data = f.read()
	data = data.split('\n')
	for i in range(len(data)):
		data[i] = data[i].split(Split_Symbol)
	f.close()
	return data

if __name__ == '__main__':
	if len(argv)==6:
		Wresult_ifile = argv[1]
		Wfeature_ifile = argv[2]
		Aresult_ifile = argv[3]
		Afeature_ifile = argv[4]
		feature_ofile = argv[5]
	else:
		print "The argument of input does not been assigned."
		print 'Argv num: '+len(argv)
		exit()


	# Ambient

	f = open(Aresult_ifile)
	dr = f.read()
	dr = dr.split('\n')
	f.close

	index = []	
	for x in dr:
		seen = False
		for y in index:
			if y == x:
				seen = True
		if seen == False:
			index.append(x)

	print 'Ambient:'
	print 'Number of clusters: '+str(len(index))

	dfl = read_dataset(Afeature_ifile,',')
	df = zip(*zip(*dfl)[0:-1])
	df = [[float(j) for j in i] for i in df]

	meanArray = []
	meanAllArray = []
	dft = zip(*df)

	print 'Number of features: '+str(len(dft))
	print 'Number of instances: '+str(len(dft[0]))


	for y in dft:
		mean = 0
		for z in index:
			i = 0
			j = 0
			for x in y:
				if dr[j] == z:
					mean += x
					i += 1
				j += 1
			if i != 0:
				mean /= i
			meanArray.append(mean)
		meanAllArray.append(meanArray)
		meanArray = []
	meanAllArray = zip(*meanAllArray)

	#print 'Number of features: '+str(len(meanAllArray[int(0)]))

	ambient = []

	t = 0
	for z in dr:
		for e in index:
			if z == e:
				line = ''
				i = 0
				for x in meanAllArray[int(e)]:
					line += str(round(x,4)) + ','
				#line += dfl[t][len(dfl[1])-1]
				#print(str(l))
				t += 1
				ambient.append(line)
				#f.write(line+str(l)+'\n')

			
	# Wearable

	f = open(Wresult_ifile)
	dr = f.read()
	dr = dr.split('\n')
	f.close

	index = []	
	for x in dr:
		seen = False
		for y in index:
			if y == x:
				seen = True
		if seen == False:
			index.append(x)

	print 'Wearble:'
	print 'Number of clusters: '+str(len(index))

	dfl = read_dataset(Wfeature_ifile,',')
	df = zip(*zip(*dfl)[0:-1])
	df = [[float(j) for j in i] for i in df]

	meanArray = []
	meanAllArray = []
	dft = zip(*df)

	print 'Number of features: '+str(len(dft))
	print 'Number of instances: '+str(len(dft[0]))


	for y in dft:
		mean = 0
		for z in index:
			i = 0
			j = 0
			for x in y:
				if dr[j] == z:
					mean += x
					i += 1
				j += 1
			if i != 0:
				mean /= i
			meanArray.append(mean)
		meanAllArray.append(meanArray)
		meanArray = []
	meanAllArray = zip(*meanAllArray)

	#print 'Number of features: '+str(len(meanAllArray[int(0)]))

	wearable = []

	
	t = 0
	for z in dr:
		for e in index:
			if z == e:
				line = ''
				i = 0
				for x in meanAllArray[int(e)]:
					line += str(round(x,4)) + ','
				line += dfl[t][len(dfl[1])-1]
				#print(str(l))
				t += 1
				wearable.append(line)
				#f.write(line+str(l)+'\n')
	

	# Merage
	print str(len(ambient))

	f = open(feature_ofile,'w')
	i = 0
	t = 0
	l = int(len(dr))
	for y in range(l-1):
		line = str(ambient[t])
		line += str(wearable[i]) + '\n'
		f.write(line)
		i += 1
		t += 10

	exit()