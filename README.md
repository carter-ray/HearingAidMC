# HearingAidMC
Serverside Minecraft Mod to modify the distance at which sound packets are sent to players.
Mostly useful for increasing the range of bells (or other sounds with low dropoff) or decreasing the range of other server initiated sounds.

# Potential Future
Serverside to define attenuation distance for clients, clientside to use specified attenuation distance.
Serverside might migrate to automatically use specified range for deciding what packets to send to what players.

Maybe options to modify volume as well, just be aware that the two are multiplied for the actual sound range.