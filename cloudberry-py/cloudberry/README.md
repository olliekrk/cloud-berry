### Dedicated Python library for cloudberry backend

#### Building package
```python3 setup.py sdist bdist_wheel```

#### Publishing new version to TestPyPi
```python3 -m twine upload --repository testpypi dist/*```

#### Publishing updated version to TestPyPi
```python3 -m twine upload --repository testpypi --skip-existing dist/*```
