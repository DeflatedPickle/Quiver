## Changelog

### Version 0.22.2-alpha:
- Added a function to install and convert server packs.
- Added a function to replace files.
- Added icons to every Button on the Toolbar of the text-editor.
- Added cut, copy, paste and delete Buttons to the Toolbar of the text-editor.
- Added a Findbar to the text-editor.
- Added a Replacebar to the text-editor.
- Added a reload function to the text-editor.
- Added a save function to the text-editor.
- Changed the line/column counter to only be one label.

### Version 0.17.6-alpha:
- Piped `.json` files through `jsonesque` first, so comments can exist in the `.json` files.
- Added an in-programme text-editor.
- Added an in-programme image-viewer.
- Fixed opening of files if the value "system" is given.
- Added a start window.
- Added a resource pack installer.
- Added a resource pack opener (opens resource packs into the program, only works with folders).
- Fixed the zoom-in cap for the image-viewer.
- Added textures to the buttons on the toolbar of the image-viewer.
- Added `try`/`except` statements around each loading of an image.
- Changed `install_pack` to install folders instead of `.zip`s.
- Added a button to install server resource packs (currently does nothing).
- Added ToolTips to Buttons on Toolbars.
- Changed the program to use default editors/viewers if `properties.json` is not found.
- Changed the image in the image-viewer to scroll with the mouse wheel.

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