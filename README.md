<p align="center">
  <img src="https://github.com/hamidrmsj/Pose_Estimation/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width=150 />
</p>
In this Repository, Lightning flavor of MoveNet Model (From Tensorflow Lite Package) is implemented as an Android app to estimate the pose of a person from camera in real-time.

## Pose Estimation Definition
Pose Estimation is the problem of localization of human joints.

Suppose we have an image or video *(=multiple images)*, the goal is estimation of the pose of persons in it. For this purpose, the following steps are performed:
1. The spatial locations of key body joints (keypoints) are detected from input image/video.
2. Keypoints are indexed by a part ID with a confidence score between 0.0 and 1.0.
3. Finally the pose of persons in image is detected.

In order to solve the problem of Pose Estimation, different Machine learning models have been developed by different people. The developed models fall into one of the following two groups:
1. **Single person Pose Estimation:** means if there are multiple persons in an image/video, the model is able to estimate the pose of just one of them.
2. **Multi-person Pose Estimation:** means if there are multiple persons in an image/video, the model is able to estimate the poses of all of them.

**MoveNet** is one of these developed machine learning models which is a Single person Pose Estimation model. MoveNet has two flavours: 1.Lightning 2.Thunder. As said, In this Repository, the Lightning flavor of MoveNet Model  is implemented as an Android app

## Used Libraries
- Tensorflow Lite
- CameraX
- Android Material Components
- Navigation Component
- Dexter runtime permissions
