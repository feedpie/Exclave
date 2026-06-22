cmake_minimum_required(VERSION 3.10)
project("hev-socks5-tunnel" C)

set(CMAKE_C_STANDARD 11)

file(GLOB_RECURSE HEV_SOURCES
    hev-socks5-tunnel/src/*.c
    hev-socks5-tunnel/src/*.S
)
list(FILTER HEV_SOURCES EXCLUDE REGEX ".*hev-wintun\\.c$")
list(FILTER HEV_SOURCES EXCLUDE REGEX ".*hev-tunnel-windows\\.c$")

file(GLOB_RECURSE YAML_SOURCES
    hev-socks5-tunnel/third-part/yaml/src/*.c
)

file(GLOB_RECURSE LWIP_SOURCES
    hev-socks5-tunnel/third-part/lwip/src/api/*.c
    hev-socks5-tunnel/third-part/lwip/src/core/*.c
    hev-socks5-tunnel/third-part/lwip/src/core/ipv4/*.c
    hev-socks5-tunnel/third-part/lwip/src/core/ipv6/*.c
    hev-socks5-tunnel/third-part/lwip/src/netif/*.c
    hev-socks5-tunnel/third-part/lwip/src/netif/ppp/*.c
    hev-socks5-tunnel/third-part/lwip/src/netif/ppp/polarssl/*.c
    hev-socks5-tunnel/third-part/lwip/src/ports/lib/*.c
    hev-socks5-tunnel/third-part/lwip/src/ports/lib/*.S
    hev-socks5-tunnel/third-part/lwip/src/ports/unix/lib/*.c
    hev-socks5-tunnel/third-part/lwip/src/ports/unix/netif/*.c
)

file(GLOB_RECURSE TASK_SOURCES
    hev-socks5-tunnel/third-part/hev-task-system/src/*.c
    hev-socks5-tunnel/third-part/hev-task-system/src/*.S
)
list(FILTER TASK_SOURCES EXCLUDE REGEX ".*hev-task-system/src/lib/rbtree/hev-rbtree\\.c$")
list(FILTER TASK_SOURCES EXCLUDE REGEX ".*hev-task-system/src/lib/list/hev-list\\.c$")

add_library(hev-socks5-tunnel SHARED
    ${HEV_SOURCES}
    ${YAML_SOURCES}
    ${LWIP_SOURCES}
    ${TASK_SOURCES}
)

target_include_directories(hev-socks5-tunnel PRIVATE
    hev-socks5-tunnel/src
    hev-socks5-tunnel/src/misc
    hev-socks5-tunnel/src/core/include
    hev-socks5-tunnel/third-part/yaml/include
    hev-socks5-tunnel/third-part/lwip/src/include
    hev-socks5-tunnel/third-part/lwip/src/ports/include
    hev-socks5-tunnel/third-part/lwip/src/ports/unix/include
    hev-socks5-tunnel/third-part/hev-task-system/include
    hev-socks5-tunnel/third-part/hev-task-system/src
)

target_compile_definitions(hev-socks5-tunnel PRIVATE
    FD_SET_DEFINED
    SOCKLEN_T_DEFINED
    ENABLE_LIBRARY
    PKGNAME=io/nekohasekai/sagernet/bg
    CLSNAME=HevTunnel
    YAML_VERSION_MAJOR=0
    YAML_VERSION_MINOR=2
    YAML_VERSION_PATCH=5
    YAML_VERSION_STRING=\"0.2.5\"
    CONFIG_STACK_BACKEND=STACK_MMAP
    CONFIG_STACK_OVERFLOW_DETECTION=1
    CONFIG_MEMALLOC_SLICE_ALIGN=16
    CONFIG_MEMALLOC_SLICE_MAX_SIZE=4096
    CONFIG_MEMALLOC_SLICE_MAX_COUNT=1000
    CONFIG_SCHED_CLOCK=CLOCK_NONE
    ENABLE_MEMALLOC_SLICE
    ENABLE_IO_SPLICE_SYSCALL
)

find_library(log-lib log)
target_link_libraries(hev-socks5-tunnel PRIVATE ${log-lib})
