import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="cloudberrypy",
    version="0.0.1",
    author="Olgierd Królik & Michał Magiera",
    author_email="dbiszkopter@gmail.com",
    description="cloudberry-py library",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/olliekrk/cloud-berry",
    packages=setuptools.find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires='>=3.7',
)
