# -*- coding: utf-8 -*-

import serial
import struct
import guippy
import time

ser = serial.Serial(3, 115200)
print ser.name
gp = guippy.Guippy()

isDragLeft = False
isDragRight = False

old_key = 0x0000

while True:
	cmd = ser.read()
	print cmd
	
	old_time = time.clock()
	if cmd == 'M':
		x,y = struct.unpack('hh', ser.read(4))
		gp.jump(x,y,True,False)
		print x,y
	elif cmd == 'R':
		gp.click(guippy.mouse.RIGHT)
	elif cmd == 'L':
		gp.click(guippy.mouse.LEFT)
	elif cmd == 'O':
		n, = struct.unpack('b', ser.read())
		gp.wheel(n)
		print n
	elif cmd == 'U':
		isDragLeft = True
		gp.drag(guippy.mouse.LEFT)
	elif cmd == 'I':
		isDragRight = True
		gp.drag(guippy.mouse.RIGHT)
	elif cmd == 'J':
		if isDragLeft: gp.drop(guippy.mouse.LEFT)
		if isDragRight: gp.drop(guippy.mouse.RIGHT)
	elif cmd == 'G':
		key, = struct.unpack('h', ser.read(2))
		print key
		
		if old_key - key <> 0:
			if key&0x2000 > 0: #START
				gp.enter()
			if key&0x1000 > 0: #ESC
				gp.escape()
			if key&0x0800 > 0: #UP
				gp.push(38)
			else:
				gp.release(38)
			if key&0x0400 > 0: #DOWN
				gp.push(40)
			else:
				gp.release(40)
			if key&0x0200 > 0: #LEFT
				gp.push(37)
			else:
				gp.release(37)
			if key&0x0100 > 0: #RIGHT
				gp.push(39)
			else:
				gp.release(39)
			if key&0x0080 > 0: #Combo1
				gp.punch('o')
				gp.push(39)
				gp.release(39)
				gp.push(40)
				gp.release(40)
				gp.push(39)
				gp.release(39)
				gp.punch('u')

				gp.push(40)
				gp.push(39)
				gp.release(40)
				gp.release(39)
				gp.push(40)
				gp.push(39)
				gp.release(40)
				gp.release(39)
				gp.punch('o')

			if key&0x0040 > 0: #P1
				gp.push(85)
			else:
				gp.release(85)
			if key&0x0020 > 0: #P2
				gp.push(73)
			else:
				gp.release(73)
			if key&0x0010 > 0: #P3
				gp.push(79)
			else:
				gp.release(79)
			if key&0x0008 > 0: #Combo2
				gp.push(40)
				gp.push(39)
				gp.release(40)
				gp.push(79)
				gp.release(39)

			if key&0x0004 > 0: #K1
				gp.push(74)
			else:
				gp.release(74)
			if key&0x0002 > 0: #K2
				gp.push(75)
			else:
				gp.release(75)
			if key&0x0001 > 0: #K3
				gp.push(76)
			else:
				gp.release(76)
			old_key = key

	elif cmd == 'E':
		break

	new_time = time.clock()
	elapsed = new_time - old_time
	print elapsed
	old_time = new_time

ser.close()

