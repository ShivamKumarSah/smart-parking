import cv2
import cvzone
import pickle
import numpy as np

width = 106
height = 48

cap = cv2.VideoCapture("carPark.mp4")

try:
    with open('CarParkPos', 'rb') as f:
        posList = pickle.load(f)
except:
    posList = []

def checking_parking_space(imgPro):
    for pos in posList:
        x,y = pos
        imgcrop = imgPro[y:y+height, x:x+width]
        count = cv2.countNonZero(imgcrop)
        cvzone.putTextRect(img, str(count), (x,y+height-3), scale=1.1, thickness=2, offset=0)
        if count < 900:
            colour = (0, 255, 0)
            thickness = 5
        else:
            colour = (0, 0, 255)
            thickness = 2

        cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), colour, thickness)

while True:
    if cap.get(cv2.CAP_PROP_POS_FRAMES) == cap.get(cv2.CAP_PROP_FRAME_COUNT):
        cap.set(cv2.CAP_PROP_POS_FRAMES, 0)

    success, img = cap.read()
    imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    imgBlur = cv2.GaussianBlur(imgGray, (3,3), 1)
    imgThreshold = cv2.adaptiveThreshold(imgBlur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                         cv2.THRESH_BINARY_INV, 25, 16)
    imgMedian = cv2.medianBlur(imgThreshold, 5)
    kernel = np.ones((3, 3), np.uint8)
    imgDilate = cv2.dilate(imgMedian, kernel, iterations=1)

    checking_parking_space(imgDilate)

    cv2.imshow("frames", img)
    # cv2.imshow("imgDilate", imgDilate)
    cv2.waitKey(5)