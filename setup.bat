pyinstaller ^
	--console ^
	--onedir ^
	--noconfirm ^
	--name="Quiver" ^
	--icon=quiver.ico ^
	--add-data="properties.json;." ^
	--add-data="quiver.ico;." ^
	--add-data="icons/;icons/" ^
	--clean ^
	--hidden-import="nbt.world" ^
	window.py
