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
	if len(argv)==3:
		feature_ifile = argv[1]
		feature_ofile = argv[2]

		
	else:
		print "The argument of input does not been assigned."
		exit()
	f = open(feature_ofile,'w')
	d = read_dataset(feature_ifile,'	')
	i = 0
	index = []
	
	for x in d:
		seen = False
		for y in index:
			if y == x[1]:
				seen = True
		if seen == False:
			index.append(x[1])
	#for x in index:
		#print(str(x)+'\n')
	for x in d:
		#f.write(str(x[0])+',')
		i = 0
		for y in index:
			if x[1] == y:
				f.write(str(i)+'\n')
			i += 1
