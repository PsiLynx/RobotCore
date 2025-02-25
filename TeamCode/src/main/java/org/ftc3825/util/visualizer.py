import numpy as np
# import pygame
import math
import sys

width = 150
height = math.floor( width / 16 * 9 )
scale = 1
wheelCount = 30
winScale = math.floor(1200 / width)
maxReps = 820
dataType = np.complex128

transX = 0.0
transY = 0.0

# transX, transY = (0, 0)
running = True
explore = True
min = -20
step = 0.5
max = 372

halfW = width / 2
halfH = height / 2
xDenom =  1 / (width * scale)
yDenom =  1 / (height * scale)

def render():
    pass
    # pygame.draw.rect(win, col(pixels[y, x]), (x * winScale, y * winScale, winScale, winScale))

# pygame.init()

# win = pygame.display.set_mode((width * winScale, height * winScale))
# clock = pygame.time.Clock()

def parse(path: str):
    path = path.replace(" ", "")
    path = "".join([ line for line in path.split("\n") if len(line) > 0 and line[0] != "/"])
    path = path.replace("\n", "")
    path = path.replace("followPath{", "")
    if(path == ""): return
    print(path)
    startIdx = path.index("start")
    commaIdx = (path[startIdx:]).index(",") + startIdx
    start = (
        path[startIdx + 6:commaIdx],
        path[commaIdx + 1:(path[commaIdx:]).index(")") + commaIdx]
    )
    print(start)





with open(sys.argv[1], "r" ) as file:
    text = "".join([ line for line in file.readlines() if line[0] != "i"])
    # print(text)
    next = 0
    while next > -1:
        try:
            next = text.index("followPath")
        except:
            next = -1
            break
        subStr = text[next:-1]
        pathStr = subStr[0:subStr.index("}") + 1]
        text = text[subStr.index("}"):-1]
        parse(pathStr)
"""
while running:
    x, y = (0, 0)
    if not explore:
        print("wheelCount: ", wheelCount)
    if explore:
        shift = False
        print("fps: {:0.1f}".format(clock.get_fps()))
        print(wheelCount, maxReps, transX, transY)
        clock.tick(60)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.MOUSEWHEEL:
                wheelCount += event.y
        keys = pygame.key.get_pressed()
        if keys[pygame.K_LEFT]:
            x -= 20 / clock.get_fps()
        if keys[pygame.K_RIGHT]:
            x += 20 / clock.get_fps()
        if keys[pygame.K_UP]:
            y -= 20 / clock.get_fps()
        if keys[pygame.K_DOWN]:
            y += 20 / clock.get_fps()
        if keys[pygame.K_EQUALS]:
            maxReps += 100
        if keys[pygame.K_MINUS]:
            maxReps -= 100

    height = math.floor( width / 16 * 9 )
    winScale = math.floor(1200 / width)

    transX += x / scale
    transY += y / scale
    scale = math.exp(wheelCount / 10)

    xDenom = 1 / (width * scale)
    yDenom = 1 / (height * scale)

    halfW = width / 2
    halfH = height / 2

    render()

    if explore:
        pygame.draw.line(win, 0x888888, (width * winScale / 2, 0), (width * winScale / 2, height * winScale))
        pygame.draw.line(win, 0x888888, (0, height * winScale / 2), ( width * winScale, height * winScale / 2))
        pygame.display.update()

"""