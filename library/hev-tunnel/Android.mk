LOCAL_PATH := $(call my-dir)

LOCAL_CFLAGS += -DPKGNAME=io/nekohasekai/sagernet/bg -DCLSNAME=HevTunnel

include $(LOCAL_PATH)/hev-socks5-tunnel/Android.mk
