# Camera Sequence Assets
A Hytale plugin which adds a `CameraSequenceAsset`, allowing camera sequences (cutscenes, etc.) to be configured and
created through the in game asset editor, or through JSON. 

These sequences can then be played back using `/camerasequence` or `/cinematic`, or using a new trigger volume effect
called `Trigger Camera Sequence`

## Player Relativity
Camera sequence assets provides a toggle on each keyframe to specify that the position and rotation values should be
interpreted as relative to the player. This means that a position of `(5, 0, 5)` refers to the point 5 blocks in front
of the player, and 5 blocks to the right. It also means that a rotation of `(0, 90, 0)` corresponds to the direction
90 degrees to the right of the player's current facing direction. This is all taken with respect to the head rotation,
not the body rotation.