# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

# 指定CMake的最小版本
cmake_minimum_required(VERSION 3.4.1)

# 设置项目名称
project(JKNF_AV)
# 过滤源文件
file(GLOB SRC_C_CXX "protocol/*.cpp" "*.c")
# 搜索src/main/cpp目录下的所有过滤后的文件
aux_source_directory(src/main/cpp SRC_C_CXX)

# 打开默认编译成共享库的开关，如果不设置默认编译成静态库
set(BUILD_SHARED_LIBS ON)

# 生成可执行文件
# add_executable(jackknife-av src/main/cpp/jknf_ffmpeg.cpp)
# 默认的搜索路径为 cmake 包含的系统库，因此如果是 NDK 的公共库只需要指定库的 name 即可
# 类似的命令还有 find_file()、find_path()、find_program()、find_package()
find_library(
        log-lib
        log)
# 设置包含的目录，头文件目录
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_BINARY_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
        ${CMAKE_SOURCE_DIR}/src/main/cpp/include
)

# 设置so动态链接库搜索目录
link_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/libs
)

set(distribution_DIR ../../../../libs)

message("添加库avutil")
add_library(avutil-55
        SHARED
        IMPORTED)
set_target_properties(avutil-55
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavutil-55.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

message("添加库swresample")
add_library(swresample-2
        SHARED
        IMPORTED)
set_target_properties(swresample-2
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswresample-2.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

message("添加库avcodec")
add_library(avcodec-57
        SHARED
        IMPORTED)
set_target_properties(avcodec-57
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavcodec-57.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)
message(${CMAKE_SOURCE_DIR}/libs/include)
message("添加库avfilter")
add_library(avfilter-6
        SHARED
        IMPORTED)
set_target_properties(avfilter-6
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavfilter-6.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

message("添加库swscale")
add_library(swscale-4
        SHARED

        IMPORTED)
set_target_properties(swscale-4
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswscale-4.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

message("添加库avdevice")
add_library(avdevice-57
        SHARED
        IMPORTED)
set_target_properties(avdevice-57
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavdevice-57.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

message("添加库avformat")
add_library(avformat-57
        SHARED
        IMPORTED)
set_target_properties(avformat-57
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavformat-57.so
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_SOURCE_DIR}/src/main/cpp/include/)

# 添加所有源代码目录打入so，不指定SHARED默认为静态库
add_library(jknfav ${SRC_C_CXX})

if (UNIX)
    message("current is UNIX")
elseif (WIN32)
    message("current is WIN32")
endif ()

target_include_directories(jknfav PRIVATE ${CMAKE_SOURCE_DIR}/src/main/cpp/)
# 设置 target 需要链接的库
target_link_libraries(jknfav log android avutil-55 swresample-2 avcodec-57 avfilter-6 swscale-4
        avdevice-57 avformat-57 ${log-lib})
# 指定全路径
# target_link_libraries(demo ${CMAKE_CURRENT_SOURCE_DIR}/libs/libface.a)
message("构建完成")

# 设置include的搜索路径，必须要设置
#set(CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)
#message(cmake文件搜索路径为->${CMAKE_MODULE_PATH})

# 指定包含文件的全路径
#include(jackknife-av.cmake)
