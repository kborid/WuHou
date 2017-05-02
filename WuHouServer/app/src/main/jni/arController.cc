/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#include <android/log.h>
#include "arController.hpp"
#include "include/frame.hpp"

namespace EasyAR {
    namespace samples {

        ARController::ARController() {
            view_size[0] = -1;
            tracked_target = 0;
            active_target = 0;
            for (int i = 0; i < 10; ++i) {
                textid[i] = 0;
                renderer[i] = new VideoRenderer;
            }
            video_renderer = NULL;
            video = NULL;
        }

        ARController::~ARController() {
            for (int i = 0; i < 10; ++i) {
                delete renderer[i];
            }
        }

        void ARController::addImageSource(std::string &image, std::string &name, bool isJson,
                                          int type, std::string &path) {
            ImageInfo temp;
            temp.image = image;
            temp.name = name;
            temp.type = (VideoType) type;
            temp.path = path;
            info.push_back(temp);
            if (isJson) {
                AR::loadAllFromJsonFile(image);
            }
            else {
                AR::loadFromImage(image);
            }
        }

        void ARController::initGL() {
            augmenter_ = Augmenter();
            augmenter_.attachCamera(camera_);
            for (int i = 0; i < 10; ++i) {
                renderer[i]->init();
                textid[i] = renderer[i]->texId();
            }
        }

        void ARController::resizeGL(int width, int height) {
            view_size = Vec2I(width, height);
        }

        void ARController::render() {
            glClearColor(0.f, 0.f, 0.f, 1.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Frame frame = augmenter_.newFrame();
            if (view_size[0] > 0) {
                AR::resizeGL(view_size[0], view_size[1]);
                if (camera_ && camera_.isOpened())
                    view_size[0] = -1;
            }
            augmenter_.setViewPort(viewport_);
            augmenter_.drawVideoBackground();
            glViewport(viewport_[0], viewport_[1], viewport_[2], viewport_[3]);

            AugmentedTarget::Status status = frame.targets()[0].status();
            if (status == AugmentedTarget::kTargetStatusTracked) {
                int id = frame.targets()[0].target().id();
                if (active_target && active_target != id) {
                    video->onLost();
                    delete video;
                    video = NULL;
                    tracked_target = 0;
                    active_target = 0;
                }
                if (!tracked_target) {
                    if (!video) {
                        int i = 0;
                        for (std::vector<ImageInfo>::iterator it = info.begin();
                             it != info.end(); it++, i++) {
                            ImageInfo temp = *it;
                            __android_log_print(ANDROID_LOG_INFO, "==============", "size = %d",
                                                info.size());
                            if (frame.targets()[0].target().name() == temp.name && textid[i]) {
                                __android_log_print(ANDROID_LOG_INFO, "==============",
                                                    "name = %s, textid = %d, type = %d, path = %s",
                                                    temp.name.c_str(), textid[i], temp.type,
                                                    temp.path.c_str());
                                video = new ARVideo;
                                switch (temp.type) {
                                    case NoTransparent:
                                        video->openVideoFile(temp.path, textid[i]);
                                        break;
                                    case Transparent:
                                        video->openTransparentVideoFile(temp.path, textid[i]);
                                        break;
                                    case NetVideo:
                                        video->openStreamingVideo(temp.path, textid[i]);
                                        break;
                                    default:
                                        break;
                                }
                                video_renderer = renderer[i];
                                break;
                            }
                        }
                    }
                    if (video) {
                        video->onFound();
                        tracked_target = id;
                        active_target = id;
                    }
                }
                Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f,
                                                             500.f);
                Matrix44F cameraview = getPoseGL(frame.targets()[0].pose());
                ImageTarget target = frame.targets()[0].target().cast_dynamic<ImageTarget>();
                if (tracked_target) {
                    video->update();
                    video_renderer->render(projectionMatrix, cameraview, target.size());
                }
            } else {
                if (tracked_target) {
                    video->onLost();
                    tracked_target = 0;
                }
            }
        }

        bool ARController::clear() {
            AR::clear();
            if (video) {
                delete video;
                video = NULL;
                tracked_target = 0;
                active_target = 0;
            }
            if (!info.empty() && info.size() > 0) {
                info.clear();
            }
            return true;
        }
    }
}