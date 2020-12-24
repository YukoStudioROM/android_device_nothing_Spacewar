/*
 * Copyright (C) 2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "vendor.lineage.fastcharge@1.0-service.nothing"

#include "FastCharge.h"

#include <android-base/logging.h>

#include <fstream>

#define FASTCHARGE_PATH "/sys/class/qcom-battery/slowcharge_en"
#define FASTCHARGE_ON 0
#define FASTCHARGE_OFF 1

namespace {

template <typename T>
static T get(const std::string& path, const T& def) {
    std::ifstream file(path);
    T result;
    file >> result;
    return file.fail() ? def : result;
}

template <typename T>
static void set(const std::string& path, const T& value) {
    std::ofstream file(path);
    file << value;
}

}  // anonymous namespace

namespace vendor {
namespace lineage {
namespace fastcharge {
namespace V1_0 {
namespace implementation {

Return<bool> FastCharge::isEnabled() {
    return get(FASTCHARGE_PATH, FASTCHARGE_OFF) != FASTCHARGE_OFF;
}

Return<bool> FastCharge::setEnabled(bool enable) {
    set(FASTCHARGE_PATH, enable ? FASTCHARGE_ON : FASTCHARGE_OFF);
    return isEnabled();
}

}  // namespace implementation
}  // namespace V1_0
}  // namespace fastcharge
}  // namespace lineage
}  // namespace vendor
