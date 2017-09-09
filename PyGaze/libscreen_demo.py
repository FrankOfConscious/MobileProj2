# example: PyGame code on PyGaze objects
# import stuff
from pygaze import libscreen
import pygame
from pygame.locals import *

# create Display object
disp = libscreen.Display(disptype='pygame',dispsize=(800,800))

# create Screen object
coolscreen = libscreen.Screen(disptype='pygame',dispsize=(800,800))

# # draw some text on the screen
# coolscreen.draw_text(text="This text is mirrored!")
#
# # mirror the text (over the vertical axis), using a PyGame function
# coolscreen.screen = pygame.transform.flip(coolscreen.screen, True, False)
#
# # now show as you would normally do
# disp.fill(screen=coolscreen)
# disp.show()