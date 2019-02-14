import cv2
import sys
import numpy as np
import time
import libjevois as jevois
import json

WIDTH = 320
HEIGHT = 240
CENTER_X = WIDTH/2
CENTER_Y = HEIGHT/2

VIEW_ANGLE = 65.0
FONT = cv2.FONT_HERSHEY_SIMPLEX
RED = (0,0,255)
GREEN = (0,255,0)
YELLOW = (0,255,255)
AA = cv2.LINE_AA
DPP = VIEW_ANGLE/WIDTH

frameCounter = 0
frameRate = 0
startTime = time.time()
FRAME_DELTA = 0.1

def prepImage(image, LOW, HIGH):
	lower_bound = np.array(LOW, dtype=np.uint8)
	upper_bound = np.array(HIGH, dtype=np.uint8)
	image = cv2.inRange(image, lower_bound, upper_bound, 255)
	image = cv2.erode(image, None, iterations=2)
	image = cv2.dilate(image, None, iterations=2)
	return image

#p is which point to select, x is x coordinate(0) or y coordinate(1)
def getCoordinate(contourList, p, x):
	return float(str(contourList[p])[2:len(str(contourList[p]))-2].split()[x])

def prepImage(image, LOW, HIGH):
	lower_bound = np.array(LOW, dtype=np.uint8)
	upper_bound = np.array(HIGH, dtype=np.uint8)
	image = cv2.inRange(image, lower_bound, upper_bound, 255)
	image = cv2.erode(image, None, iterations=2)
	image = cv2.dilate(image, None, iterations=2)
	return image

def approxContour(contour):
	perimeter = cv2.arcLength(contour, True)
	epsilon = 0.027*cv2.arcLength(contour, True)
	return cv2.approxPolyDP(contour, epsilon, True)

def bubbleSort(List):
	for passnum in range(len(List)-1,0,-1):
		for i in range(passnum):
			if int(str(List[i])[1:len(str(List[i]))-2].split()[1])>int(str(List[i+1])[1:len(str(List[i+1]))-2].split()[1]):
				temp = List[i]
				List[i] = List[i+1]
				List[i+1] = temp

class FRC2019:
	def __init__(self):
		a = 5
	
	def process(self, inframe, outframe):
		global frameCounter;
		global frameRate;
		global startTime;
		if(time.time()-startTime > FRAME_DELTA):
			frameRate = frameCounter/(time.time()-startTime)
			startTime = time.time()
			frameCounter = 0
		
		# Capture frame-by-frame
		frame = inframe.getCvBGR()

		#BGR Filter values, VALUES are bgr, NOT RGB
		gamerImage = prepImage(frame.copy(), [240, 240, 20], [255, 255, 200])

		ballImage = prepImage(frame.copy(), [0, 0, 40], [40, 50, 160])

		bcopy = ballImage.copy()
		a, ballContours, b = cv2.findContours(bcopy, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
		
		#draw contours to another copy
		fcopy = gamerImage.copy()
		a, contours, b = cv2.findContours(fcopy, cv2.RETR_LIST,cv2.CHAIN_APPROX_SIMPLE)
		
		if len(ballContours) > 1:
		# find the largest contour in the mask, then use
		# it to compute the minimum enclosing circle and
		# centroid
			c = max(ballContours, key=cv2.contourArea)
			((x, y), radius) = cv2.minEnclosingCircle(c)
			M = cv2.moments(c)
			ballCenter = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
	 
			# only proceed if the radius meets a minimum size
			if radius > 10:
				# draw the circle and centroid on the frame,
				# then update the list of tracked points
				cv2.circle(frame, (int(x), int(y)), int(radius), (0, 255, 255), 2)
				cv2.circle(frame, ballCenter, 5, (0, 0, 255), -1)

				targetAngleBall = ballCenter*DPP
				jevois.sendSerial("B" + targetAngleBall + ",")
		try:
			if len(contours) > 1:
				cv2.drawContours(frame, contours, -1, GREEN, 3)
				area = 0
				area2 = 0;
				largestContours = [contours[0], contours[1], contours[2]]
				
				for passnum in range(len(contours)-1,0,-1):
					for i in range(passnum):
						if cv2.contourArea(contours[i])<cv2.contourArea(contours[i+1]):
							temp = contours[i]
							contours[i] = contours[i+1]
							contours[i+1] = temp
				
				largestContours[0] = contours[0]
				largestContours[1] = contours[1]
				largestContours[2] = contours[2]
				
				try:
					approximations = [approxContour(largestContours[0]), approxContour(largestContours[1]), approxContour(largestContours[2])]
					
					pointsA = [approximations[0][0], approximations[0][1], approximations[0][2], approximations[0][3]]
					pointsB = [approximations[1][0], approximations[1][1], approximations[1][2], approximations[1][3]]
					pointsC = [approximations[2][0], approximations[2][1], approximations[2][2], approximations[2][3]]
					points = [pointsA, pointsB, pointsC]
					#sorting all three sets of points, bubble sort by y in ascending order
					#Yes i know bubble sort sucks but its for literally 4 points 3 times and a single contour sort
					
					bubbleSort(pointsA)
					bubbleSort(pointsB) #b i g LAG
					bubbleSort(pointsC)
					
					aSlope = (getCoordinate(pointsA, 1, 1) - getCoordinate(pointsA, 0, 1)) / (getCoordinate(pointsA, 1, 0) - getCoordinate(pointsA, 0, 0))
					bSlope = (getCoordinate(pointsB, 1, 1) - getCoordinate(pointsB, 0, 1)) / (getCoordinate(pointsB, 1, 0) - getCoordinate(pointsB, 0, 0))
					cSlope = (getCoordinate(pointsC, 1, 1) - getCoordinate(pointsC, 0, 1)) / (getCoordinate(pointsC, 1, 0) - getCoordinate(pointsC, 0, 0))
					slopes = [aSlope, bSlope, cSlope]
					minSlope = 99999
					slopeIndex = 0

					if slopes[0] < minSlope:
						minSlope = slopes[0]
						slopeIndex = 0

					if slopes[1] < minSlope:
						minSlope = slopes[1]
						slopeIndex = 1

					if slopes[2] < minSlope:
						minSlope = slopes[2]
						slopeIndex = 2

					match = [0, 0]
					if slopeIndex == 0:
						match[0] = 0
						if cv2.contourArea(approximations[1]) > cv2.contourArea(approximations[2]):
							match[1] = 1
						else:
							match[1] = 2

					if slopeIndex == 1:
						match[0] = 1
						if cv2.contourArea(approximations[2]) > cv2.contourArea(approximations[0]):
							match[1] = 2
						else:
							match[1] = 0

					if slopeIndex == 2:
						match[0] = 2
						if cv2.contourArea(approximations[0]) > cv2.contourArea(approximations[1]):
							match[1] = 0
						else:
							match[1] = 1

					targetCoordinateTape = (getCoordinate(points[match[0]], 0, 0) + getCoordinate(points[match[1]], 0, 0))/2

					targetAngleTape = targetCoordinateTape*DPP

					jevois.sendSerial("T" + targetAngleTape + ",")

					frameCounter += 1
				except Exception as E:
					frameCounter -= 1
		except Exception as E:
			frameCounter -= 1

		cv2.putText(frame, "FPS: " + str(int(frameRate)), (0, HEIGHT-4), FONT, 0.5, (255,255,255), 2)

		outframe.sendCv(frame)