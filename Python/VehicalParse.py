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
		input_file = argv[1]
		feature_file = argv[2]
		#report_file = argv[3]

		
	else:
		print "The argument of input does not been assigned."
		exit()
	f = open(feature_file,'w')
	d = read_dataset(input_file,' ')
	#d = zip(*zip(*d)[0:-1])
	#i = 0
	#l = len(d)
	#i = 0
	for y in d:
		i = 0
		t = ''
		for x in y:
			if i == 0:
				t = str(x)
			if i%2 == 0 and i != 0:
				f.write(str(x)+',')
			i += 1
		f.write(t+'\n')
