import pygame
import math
import sys
import numpy as np
from inotify_simple import INotify, flags

inotify = INotify()

width = 1000
height = width
scale = 1
wheelCount = 20
winScale = 1
maxReps = 820

transX = 0.0
transY = 0.0

# transX, transY = (0, 0)
running = True
explore = True
min = -20
step = 0.5
max = 372

segments = []

def add(a: tuple, b: tuple) -> tuple:
    return (
        a[0] + b[0], a[1] + b[1]
    )
def sub(a: tuple, b: tuple) -> tuple:
    return (
        a[0] - b[0], a[1] - b[1]
    )
def mul(a: tuple, b: float) -> tuple:
    return (
        a[0] * b, a[1] * b
    )

def spline(cp: list[tuple], t: float):
    p1, cp1, cp2, p4 = (
        np.array(cp[0]), np.array(cp[1]), np.array(cp[2]), np.array(cp[3])
    )

    v1 = p1 + cp1
    v2 = p4 - cp2

    point = (
        p1
        + ( - p1*3.0 + v1*3.0               ) * t
        + (   p1*3.0 - v1*6.0 + v2*3.0      ) * t * t
        + (- p1 + v1 * 3.0 - v2 * 3.0 + p4 ) * t * t * t
    )

    return point[0], point[1]

def line(start, end, color: int, stroke: int):
    pygame.draw.line(
        win, color,
        (
            (start[0] + transX) * scale + width / 2,
            - (start[1] + transY) * scale + height / 2
        ),
        (
            (end[0] + transX) * scale + width / 2,
            - (end[1] + transY) * scale + height / 2
        ),
        stroke
    )

def render():
    for path in segments:
        if len(path) == 2:
            line(path[0], path[1], 0xc8ffc8, 5)
        else:
            for i in range(100):
                line(
                    spline(path, i / 100.0),
                    spline(path, (i + 1) / 100.0),
                    0xc8ffc8,
                    5
                )

pygame.init()

win = pygame.display.set_mode(
    (width * winScale, height * winScale),
    pygame.RESIZABLE
)
image = pygame.image.load("field.png").convert()
image = pygame.transform.scale(
    image,
    (
        144 * 4,
        144 * 4
    )
)

def indexOf(string: str, toFind: str, count: int):
    index = -1
    soFar = 0

    while soFar < count:
        try:
            index = string.index(toFind, index + 1)
            soFar += 1
        except ValueError:
            return None  # If `toFind` isn't found, return None.

    return index

def parse(input: str):
    path = [
        line.replace(" ", "") for line in input.split("\n")
        if len(line) > 0 and line[0] != "/"
    ]
    path = "".join([
        line[0:indexOf(line, "//", 1)] for line in path

    ])
    path = path.replace("\n", "")
    path = path.replace(" ", "")
    path = path.replace("followPath{", "")
    if(path == ""): return
    startIdx = path.index("start")
    commaIdx = (path[startIdx:]).index(",") + startIdx
    start = (
        float(path[startIdx + 6:commaIdx]),
        float(path[commaIdx + 1:(path[commaIdx:]).index(")") + commaIdx])
    )
    path = path[path.index(")") + 1:]
    while len(path) > 0:
        try:
            if(path[0] == "l"): # line to
                path = path[7:]
                next = (
                    float(path[0:path.index(",")]),
                    float(path[
                        indexOf(path, ",", 1) + 1:
                        indexOf(path, ",", 2)
                    ])
                )
                segments.append([start, next])
                start = next
                path = path[path.index(")") + 2:]
            elif(path[0] == "c"): # curve to
                path = path[8:]
                print(path)
                cp1 = (
                    float(path[0:path.index(",")]),
                    float(path[
                        indexOf(path, ",", 1) + 1:
                        indexOf(path, ",", 2)
                    ])
                )
                cp2 = (
                    float(path[
                        indexOf(path, ",", 2) + 1:
                        indexOf(path, ",", 3)
                    ]),
                    float(path[
                        indexOf(path, ",", 3) + 1:
                        indexOf(path, ",", 4)
                    ])
                )
                next = (
                   float(path[
                       indexOf(path, ",", 4) + 1:
                       indexOf(path, ",", 5)
                   ]),
                   float(path[
                      indexOf(path, ",", 5) + 1:
                      indexOf(path, ",", 6)
                   ])
                )
                segments.append([start, cp1, cp2, next])
                print([start, cp1, cp2, next])
                start = next
                path = path[path.index(")") + 2:]
            elif(path[0] == "}"): break
        except Exception as e:
            print(e)
            path = path[path.index(")") + 2:]
            pass


def update():
    global segments
    segments = []
    with open(sys.argv[1], "r" ) as file:
        text = "".join([ line for line in file.readlines() if line[0] != "i"])
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

inotify.add_watch(sys.argv[1], flags.MODIFY)
update()

while running:
    x, y = (0, 0)
    for event in inotify.read(timeout=0.01):
        if event.mask & flags.MODIFY:
            update()

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        elif event.type == pygame.MOUSEWHEEL:
            wheelCount += event.y
    keys = pygame.key.get_pressed()
    if keys[pygame.K_LEFT]:
        x += 500 / 60
    if keys[pygame.K_RIGHT]:
        x -= 500 / 60
    if keys[pygame.K_UP]:
        y -= 500/ 60
    if keys[pygame.K_DOWN]:
        y += 500 / 60
    if keys[pygame.K_EQUALS]:
        maxReps += 100
    if keys[pygame.K_MINUS]:
        maxReps -= 100

    transX += x / scale
    transY += y / scale
    scale = math.exp(wheelCount / 10)

    scaled = pygame.transform.scale(
        image,
        (
            image.get_width() * scale / 4,
            image.get_height() * scale / 4
        )
    )

    win.fill((0, 0, 0))
    win.blit(
        scaled,
        scaled.get_rect(
            center = (
                transX * scale + width / 2,
                -transY * scale + width / 2
            )
        )
    )
    render()
    # line((0, -72), (0, 72), 0xffffff, 1)
    # line((-72, 0), (72, 0), 0xffffff, 1)

    # line((-72, -72), (-72, 72), 0xffffff, 1)
    # line((-72, 72), (72, 72), 0xffffff, 1)
    # line((72, 72), (72, -72), 0xffffff, 1)
    # line((72, -72), (-72, -72), 0xffffff, 1)
    pygame.display.flip()
