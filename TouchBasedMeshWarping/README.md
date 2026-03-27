# Touch-based Mesh Warping

This project is an Android application that implements touch-based mesh warping using OpenGL for real-time image deformation. The application allows users to interact with an image by applying deformations such as forward warping, restoring, bloating, and wrinkling through intuitive touch gestures.

## Features
- **Real-Time Deformation**: Deform images using touch gestures with immediate visual feedback.
- **Adjustable Parameters**: Modify deformation intensity and radius dynamically.
- **Undo/Redo Functionality**: Easily revert or reapply deformation steps.
- **Restoration**: Reset deformations to restore the original image.

## Screenshots

https://github.com/user-attachments/assets/cbaec3a0-a061-4174-bc61-59cdf4f24088

## Technologies Used
- **Programming Language**: Kotlin
- **UI Framework**: Android Jetpack Components and Material Design
- **Graphics**: OpenGL with `ImageGLSurfaceView`

## Prerequisites
- Android Studio Arctic Fox or later
- Minimum Android SDK Version: 21 (Lollipop)

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/epegasus/Touch-Based-Mesh-Warping-OpenGl.git
   ```
2. Open the project in Android Studio.
3. Build the project to resolve dependencies.
4. Run the app on an emulator or a physical device.

## Code Overview

### Layout
The main layout is defined in `activity_main.xml`. It includes:
- `ImageGLSurfaceView` for OpenGL rendering of the image.
- Material Buttons for controlling deformation modes and resetting the image.
- SeekBar for adjusting intensity levels.

### Main Components
- **`MainActivity`**: Handles UI interactions and manages the lifecycle of OpenGL rendering.
- **`DeformManager`**: Manages deformation logic, including handling touch gestures and updating OpenGL states.
- **OpenGL Wrappers**: Utilizes `CGEDeformFilterWrapper` to apply filters for image deformation.

### Key Functions
- `onSurfaceCreated`: Initializes the OpenGL surface and loads the image.
- `radiusIncClicked` / `radiusDecClicked`: Adjusts deformation radius.
- `intensityIncClicked` / `intensityDecClicked`: Adjusts deformation intensity.
- `onModeClick`: Cycles through available deformation modes.

### Deformation Modes
- **Forward**: Pushes pixels forward.
- **Restore**: Reverts pixels to their original state.
- **Bloat**: Expands pixels outward.
- **Wrinkle**: Contracts pixels inward.

## Usage
1. Launch the app.
2. Use touch gestures on the image to deform it in the selected mode.
3. Adjust radius and intensity using the respective buttons.
4. Undo, redo, or reset the deformations as needed.

## Contributing
Contributions are welcome! Feel free to submit issues or pull requests.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

---

**Acknowledgments**
- Built with inspiration from image editing and deformation tools.

