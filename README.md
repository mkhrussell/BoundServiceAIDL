# BoundServiceAIDL

This repository contains two Android applications that communicate through AIDL:

- `ImageDownloaderService`: an exported bound service app that downloads image bytes from a URL
- `ImageDownloaderClient`: a client app that binds to the service and displays the downloaded image

## Projects

### ImageDownloaderService

The service app owns the download work and exposes it through a bound service.

- App ID: `com.kamrul.imagedownloader.service`
- Service action: `com.kamrul.imagedownloader.service.action.BIND_IMAGE_DOWNLOAD_SERVICE`
- AIDL contract: `IImageDownloadService`

The service implementation lives in:

- [ImageDownloadService.kt](ImageDownloaderService/app/src/main/java/com/kamrul/imagedownloader/service/ImageDownloadService.kt)
- [AndroidManifest.xml](ImageDownloaderService/app/src/main/AndroidManifest.xml)

Its responsibility is intentionally narrow:

- accept a URL through AIDL
- download the image over HTTP
- return the raw `byte[]` result to the client

### ImageDownloaderClient

The client app binds to the remote service and renders the result in Compose.

- App ID: `com.kamrul.imagedownloader.client`
- Queries package: `com.kamrul.imagedownloader.service`

The client uses:

- Hilt for dependency injection
- KSP for Hilt annotation processing
- Clean Architecture for separation of concerns
- MVI for presentation state management

Key files:

- [MainActivity.kt](ImageDownloaderClient/app/src/main/java/com/kamrul/imagedownloader/client/ui/MainActivity.kt)
- [DownloadViewModel.kt](ImageDownloaderClient/app/src/main/java/com/kamrul/imagedownloader/client/presentation/download/DownloadViewModel.kt)
- [RemoteImageDownloadServiceDataSource.kt](ImageDownloaderClient/app/src/main/java/com/kamrul/imagedownloader/client/data/datasource/RemoteImageDownloadServiceDataSource.kt)

## Architecture

### Service architecture

The service app is a simple IPC provider.

1. The client binds using the explicit service action and package name.
2. Android returns the `IBinder`.
3. The generated AIDL stub converts the binder into `IImageDownloadService`.
4. The service downloads the image and returns the bytes across process boundaries.

### Client architecture

The client follows Clean Architecture with MVI:

- `data`
  - manages the remote Android service connection
  - performs the AIDL call
- `domain`
  - defines repository abstractions
  - exposes use cases for connect, observe service state, and download image
- `presentation`
  - defines `DownloadIntent`, `DownloadPartialState`, and `DownloadViewState`
  - reduces partial state changes into a single immutable UI state
- `ui`
  - renders Compose UI from `DownloadViewState`
  - sends user actions back as intents

### MVI flow

1. `MainActivity` sends `DownloadIntent.Initialize`.
2. `DownloadViewModel` triggers service binding through a use case.
3. Service connection changes are observed and reduced into `DownloadViewState`.
4. The user enters a URL and sends `DownloadIntent.DownloadImageClicked`.
5. The ViewModel executes the download use case.
6. Success or failure is reduced into the next UI state.

## AIDL contract

Both apps include the same AIDL file:

- [IImageDownloadService.aidl](ImageDownloaderClient/app/src/main/aidl/com/kamrul/imagedownloader/service/aidl/IImageDownloadService.aidl)
- [IImageDownloadService.aidl](ImageDownloaderService/app/src/main/aidl/com/kamrul/imagedownloader/service/aidl/IImageDownloadService.aidl)

Current contract:

```aidl
interface IImageDownloadService {
    byte[] downloadImage(String imageUrl);
}
```

## Usage

### Build

Build the service app:

```powershell
cd ImageDownloaderService
./gradlew.bat :app:assembleDebug
```

Build the client app:

```powershell
cd ImageDownloaderClient
./gradlew.bat :app:assembleDebug
```

### Install and run

1. Install `ImageDownloaderService` first.
2. Install `ImageDownloaderClient`.
3. Launch the client app.
4. Enter an image URL.
5. Tap `Download image`.

The client will bind to the service, request the image through AIDL, and render the returned bytes as a preview.

## Notes

- The service must be installed on the device before the client can connect.
- The client uses an explicit package and action to bind to the service.
- The service app requires `INTERNET` permission because the download happens in the service process.
