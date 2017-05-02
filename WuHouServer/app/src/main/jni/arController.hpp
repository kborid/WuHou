/**
* Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
* EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
* and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
*/

#ifndef __EASYAR_SAMPLE_UTILITY_ARCONTROLLER_H__
#define __EASYAR_SAMPLE_UTILITY_ARCONTROLLER_H__

#include "ar.hpp"
#include "renderer.hpp"
#include "include/augmenter.hpp"
#include "include/utility.hpp"
#include <GLES2/gl2.h>
#include <vector>

namespace EasyAR {
    enum VideoType {
        NoTransparent,
        Transparent,
        NetVideo,
    };

    struct ImageInfo {
        std::string image;
        std::string name;
        VideoType type;
        std::string path;
    };

    namespace samples {
        class ARController : public AR {
        public:
            ARController();

            ~ARController();

            virtual void initGL();

            virtual void resizeGL(int width, int height);

            virtual void render();

            virtual bool clear();

            void addImageSource(std::string &image, std::string &name, bool isJson, int type,
                               std::string &path);
        private:
            Vec2I view_size;
            int tracked_target;
            int active_target;
            ARVideo *video;
            VideoRenderer *video_renderer;
            VideoRenderer *renderer[10];
            int textid[10];
            std::vector<ImageInfo> info;
        };
    }
}
#endif
