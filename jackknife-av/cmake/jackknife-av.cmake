# 设置项目名称
project(jackknife-av)
# 过滤源文件
file(GLOB SRC_LIST "protocol/*.cpp" "*.c")
# 搜索src/main/cpp目录下的所有过滤后的文件
aux_source_directory(src/main/cpp SRC_LIST)

# 打开默认编译成共享库的开关，如果不设置默认编译成静态库
set(BUILD_SHARED_LIBS ON)

# 添加所有源代码目录打入so，不指定SHARED默认为静态库
add_library(jknfav SHARED ${SRC_LIST})
# 生成可执行文件
# add_executable(jackknife-av src/main/cpp/jknf_codec.cpp)
# 默认的搜索路径为 cmake 包含的系统库，因此如果是 NDK 的公共库只需要指定库的 name 即可
# 类似的命令还有 find_file()、find_path()、find_program()、find_package()
find_library(
              log-lib
              log )
# 设置包含的目录
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
#        ${CMAKE_CURRENT_BINARY_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# 设置链接库搜索目录
link_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/libs
)

set(distribution_DIR ../../../../libs)

message("添加库avutil")
add_library( avutil-55
             SHARED
             IMPORTED )
set_target_properties( avutil-55
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavutil-55.so )

message("添加库swresample")
add_library( swresample-2
             SHARED
             IMPORTED )
set_target_properties( swresample-2
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswresample-2.so )

message("添加库avcodec")
add_library( avcodec-57
             SHARED
             IMPORTED )
set_target_properties( avcodec-57
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavcodec-57.so )

message("添加库avfilter")
add_library( avfilter-6
             SHARED
             IMPORTED)
set_target_properties( avfilter-6
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavfilter-6.so )

message("添加库swscale")
add_library( swscale-4
             SHARED
             IMPORTED)
set_target_properties( swscale-4
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libswscale-4.so )

message("添加库avdevice")
add_library( avdevice-57
             SHARED
             IMPORTED)
set_target_properties( avdevice-57
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavdevice-57.so )

message("添加库avformat")
add_library( avformat-57
             SHARED
             IMPORTED)
set_target_properties( avformat-57
                       PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libavformat-57.so )


message("添加库fmodL")
add_library(
        fmodL
        SHARED
        IMPORTED
)
set_target_properties(
        fmodL
        PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/${ANDROID_ABI}/libfmodL.so
)

if(UNIX)
    message("当前编译环境为UNIX")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
elseif(WIN32)
    message("当前编译环境为Win32")
endif()

include_directories(libs/include)

# target_include_directories(jknfav PRIVATE libs/include)
# 设置 target 需要链接的库
target_link_libraries( jknfav log android avutil-55 swresample-2 avcodec-57 avfilter-6 swscale-4 avdevice-57 avformat-57
                       fmodL ${log-lib} )
# 指定全路径
# target_link_libraries(demo ${CMAKE_CURRENT_SOURCE_DIR}/libs/libface.a)
message("构建完成")