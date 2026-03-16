package com.kamrul.imagedownloader.service.aidl;

interface IImageDownloadService {
    byte[] downloadImage(String imageUrl);
}
