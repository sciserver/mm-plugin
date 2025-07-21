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

# This book is helpful for writing plugins
https://imagingbook.com
https://link.springer.com/book/10.1007/978-3-031-05744-1, Appendix I



### Developing

The plugin code is in the `flfm` directory.

To check formatting, run

```bash
cd flfm
mvn spotless:check
```

To format the code, run

```bash
cd flfm
mvn spotless:apply
```

Models need to go into `src/main/resources/models` they should be named
according the number of iters they perform. For example, `model_1.pt` will
come up in the dropdown as `1`.


To ensure that the GPU can be seen when running from ImageJ, start with the following code snippet:
```bash
./ImageJ -cp ./plugins/flfm_plugin.jar
```

Docker Development:

Make sure to have the [NVIDIA Container Toolkit is installed](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html) 


[Configure](https://docs.nvidia.com/datacenter/cloud-native/container-toolkit/latest/install-guide.html#configuration) the Container toolkit.


Build the docker image:
```bash
docker build --tag=mm:latest -f ./docker/Dockerfile.mm .
```




Can run the docker image with [x11docker](https://github.com/mviereck/x11docker?tab=readme-ov-file#installation)
```bash
x11docker --desktop --user=RETAIN --verbose mm:latest
```
