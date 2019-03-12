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

def withinTolerance(a, b, tolerance):
	if abs(a-b) < tolerance*((abs(a)+abs(b))/2):
		return True
	return False

def xPos(part):
	return part[0][0]

class FRC2019:
	def __init__(self):
		self.frame = 0
	
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

		tapeContours = tape(frame)
		cv2.drawContours(frame, tapeContours, -1, GREEN, 3)

		cv2.putText(frame, "FPS: " + str(int(frameRate)), (0, HEIGHT-4), FONT, 0.5, (255,255,255), 2)
		outframe.sendCv(frame)

	def processNoUSB(self, inframe):
		global frameCounter;
		global frameRate;
		global startTime;
		if(time.time()-startTime > FRAME_DELTA):
			frameRate = frameCounter/(time.time()-startTime)
			startTime = time.time()
			frameCounter = 0
		
		# Capture frame-by-frame
		frame = inframe.getCvBGR()

		tape(frame)

		self.frame += 1

	def ball(frame):
		ballImage = prepImage(frame.copy(), [5, 8, 80], [85, 115, 255])
		bcopy = ballImage.copy()
		ballContours, b = cv2.findContours(bcopy, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
		cv2.drawContours(frame, ballContours, -1, YELLOW, 3)
		
		if len(ballContours) > 1:
			cleanedBall = []
			for i in range(len(ballContours)):
				((x, y), radius) = cv2.minEnclosingCircle(ballContours[i])
				if(withinTolerance(radius*radius*3.1415, cv2.contourArea(ballContours[i]), 0.3)):
					cleanedBall.append(ballContours[i])
			# find the largest contour in the mask, then use
			# it to compute the minimum enclosing circle and
			# centroid
			c = max(ballContours, key=cv2.contourArea)
			((x, y), radius) = cv2.minEnclosingCircle(c)
			M = cv2.moments(c)
			ballCenter = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))
			ballX = int(M["m10"] / M["m00"])
	 
			# only proceed if the radius meets a minimum size
			if(radius > 10):
				# draw the circle and centroid on the frame,
				# then update the list of tracked points
				cv2.circle(frame, (int(x), int(y)), int(radius), (0, 255, 255), 2)
				cv2.circle(frame, ballCenter, 5, (0, 0, 255), -1)

				targetAngleBall = (ballX*DPP) - VIEW_ANGLE/2
				jevois.sendSerial("B" + str(targetAngleBall) + ",")

	def tape(frame):
		#BGR Filter values, VALUES are bgr, NOT RGB
		gamerImage = prepImage(frame.copy(), [240, 240, 20], [255, 255, 200])
		
		#draw contours to another copy
		fcopy = gamerImage.copy()
		contours, b = cv2.findContours(fcopy, cv2.RETR_LIST,cv2.CHAIN_APPROX_SIMPLE)
		if len(contours) > 1:
			try:
				rectangular = []
				for i in range(len(contours)):
					t = cv2.minAreaRect(contours[i])
					if t[2] > 180:
						t[2] -= 180
					if withinTolerance(t[1][0]*t[1][1], cv2.contourArea(t), 0.2) and abs(t[2]) < 30 and withinTolerance(t[2][0]/t[2][1], 0.364, 0.3):
						rectangular.append(cv2.minAreaRect(contours[i]))

				sorted(rectangular, key=xPos, reverse=True)

				if len(slopes) == 2:
					targetCoordinateTape = (rectangular[0][0][0] + rectangular[1][0][0]) / 2
					targetAngleTape = (targetCoordinateTape*DPP) - VIEW_ANGLE/2
					#jevois.sendSerial("T" + str(targetAngleTape) + ",")
					jevois.sendSerial(str(targetAngleTape))
				elif len(slopes) > 2:
					biggestThree = [rectangular[0], rectangular[1], rectangular[2]]
					specialIndex = 0
					if withinTolerance(biggestThree[0][2], biggestThree[1][2], 0.2):
						specialIndex = 2
					if withinTolerance(biggestThree[0][2], biggestThree[2][2], 0.2):
						specialIndex = 1
					lowestDist = 99999
					matchIndex = 0
					for i in range(len(biggestThree)):
						curDist = sqrt(pow(biggestThree[specialIndex][0][0], 2) - pow(biggestThree[i][0][0], 2))
						if curDist < lowestDist and i != specialIndex:
							lowestDist = curDist
							matchIndex = i

					targetCoordinateTape = (sortedPoints[matchIndex][0][0] + sortedPoints[specialIndex][0][0]) / 2
					targetAngleTape = (targetCoordinateTape*DPP) - VIEW_ANGLE/2
					#jevois.sendSerial("T" + str(targetAngleTape) + ",")
					jevois.sendSerial(str(targetAngleTape))

					frameCounter += 1
					return contours
				except Exception as E:
					frameCounter -= 1