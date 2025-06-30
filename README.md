# mm-plugin


### Getting Started

If you're running on linux you need to build the source code to install
micromanager. If you're running on macOS or Windows you can use the prebuilt
binaries.

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
