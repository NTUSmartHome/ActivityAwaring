import numpy as np
from sys import argv

def pca(X):
    cov_mat = np.cov(X.T)
    eig_val_cov, eig_vec_cov = np.linalg.eig(cov_mat)
    eig_pairs_cov = [(np.abs(eig_val_cov[i]), eig_vec_cov[:,i]) for i in range(len(eig_val_cov))]
    eig_pairs_cov = [[i[0],list(i[1])] for i in eig_pairs_cov]
    eig_pairs_cov.sort()
    eig_pairs_cov.reverse()
    return eig_pairs_cov

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
		output_file = argv[2]

		
	else:
		print "The argument of input does not been assigned."
		exit()
	d = read_dataset(input_file,' ')
	d = [[float(j) for j in i] for i in d]
	d_pca = pca(np.array(d))
	d_eigenvalue = [i[0] for i in d_pca]
	d_eigenvector = [i[1] for i in d_pca]
	f = open(output_file,'w')
	for i in d_pca:
		f.write('Eigen value: '+str(i[0]))
		f.write('\n')
		f.write('\t'+str(i[1]).replace('[','').replace(']',''))
		f.write('\n\n')
