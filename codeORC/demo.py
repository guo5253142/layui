import cv2
import numpy as np
import os

def recognize(imgpath,model_dir):
   '''
   :param imgpath:图像路径
   :param model_dir:模型参数路径
   :return:字母串
   '''

   if not os.path.exists(imgpath):
       return 'image not exists'

   if not os.path.exists(model_dir):
       return 'model file not exists'


   raw_image = cv2.imread(imgpath)

   #加载参数
   wf = open(os.path.join(model_dir,'weight.txt'), 'r')
   w = wf.readlines()
   bf = open(os.path.join(model_dir, 'bias.txt'), 'r')
   b = bf.readlines()
   w_np = np.reshape(np.array(list(map(float, w[0].split(',')[:-1]))),[900, 36])
   b_np = np.reshape(np.array(list(map(float, b[0].split(',')[:-1]))),[36])

   #
   label_map = {}
   for i in range(36):
       if i < 26:
           label_map[i] = chr(i + 97)
       else:
           label_map[i] = '%d'%(i-26)


   #由于字符间隔大致相等所以等长分割即可
   letter = []
   img_gray = cv2.cvtColor(raw_image, cv2.COLOR_BGR2GRAY)
   letter.append(img_gray[:,14:44])
   letter.append(img_gray[:,45:74])
   letter.append(img_gray[:,74:104])
   letter.append(img_gray[:,104:])

   cv2.imwrite('./test.jpg', img_gray[:,45:74])
   str = ''
   for i in range(4):
      img_gray_scaled = cv2.resize(letter[i], (30, 30))
      data = np.reshape(img_gray_scaled, [900])
      output = np.matmul(data, w_np) + b_np
      str+=label_map[np.argmax(output, 0)]


   return str

code = recognize('D:\\code\\code.png','D:\\code\\codeORC\\model')
print(code)

file_handle=open('D:\\code\\result.txt',mode='w')
file_handle.write(code)



