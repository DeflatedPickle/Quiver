## Changelog

### Version 0.13.2-alpha:
- Piped `.json` files through `jsonesque` first, so comments can exist in the `.json` files.
- Added an in-programme text-editor.
- Added an in-programme image-viewer.
- Fixed opening of files if the value "system" is given.
- Added a start window.
- Added a resource pack installer.
- Added a resource pack opener (opens resource packs into the program, only works with folders).

### Version 0.8.9-alpha:
- Switched to use `Pillow`, `PIL.Image` and `PIL.ImageTk` classes instead of the `tkinter.PhotoImage` class.
- Moved images into a folder and switched to use those images instead of `base64` strings.
- Added a maximum size for the `project_window`.
- Added a mod detector.
- Changed the mod detector button to disable itself if no mods are found.
- Added grid to images.
- Added toggles for the image chessboard and grid.
- Added exception statements for if the `properties.json` file is not found and for if the style given in the `properties.json` is not valid.
- Configured a good part of the code for it to work with `OSX`.
- Added an except statement for if no mods folder is found.
- Changed the program to use `subprocess` to open files when on `OSX`.
- Fixed an overlap of the variable, "`widget_frame_text`".
- Added a label to show the size of the image.
- Added a previous and next button to the search bar (these currently do nothing yet).
- Added a window to tell the user the pack exists, if it exists and added a window to allow the pack to be replaced with a new one.
- Added an about window.

### Version 0.1.0-alpha:
- Initial release.