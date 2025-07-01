# mm-plugin

Plug-in guidance for developing MicroManager plugins can be found
[here](https://micro-manager.org/Writing_plugins_for_Micro-Manager).


# Getting Started

If you're running on linux you need to build the source code to install
micromanager. If you're running on macOS or Windows you can use the prebuilt
binaries.

## Install a More Current Version of MicroManager

### Building the Source (Linux)

You can build source code using the provided Dockerfile. This build is the
ImageJ plugin version of Micro-Manager.

```bash
docker build -f ./docker/Dockerfile.mm -t mm-ij .
```

If you run the image, it will copy ImageJ and the plugin to the `/dest`
directory. If you mount a local directory to `/dest`, it will copy the files
there.

```bash
mkdir -p mm_install
docker run --rm -v $(pwd)/mm_install:/dest mm-ij
```


### Windows

The prebuilt binaries for Windows can be downloaded from the [releases
page](https://download.micro-manager.org/nightly/2.0/Windows/)

```powershell
mkdir -p mm_install
curl -o mm_install/MMSetup.exe https://download.micro-manager.org/nightly/2.0/Windows/MMSetup_64bit_2.0.3_20250629.exe
.\mm_install\MMSetup.exe
```

You may need to restart the computer after installation.

## Get the MicroManager Source Code

Run the following command to get the micromanager source code in the `micro-manager` directory:

```bash
git submodule update --init --recursive
```


## Set the required environment variables

- `MM_INSTALL_DIR`: should be the installation directory of MicroManager.

