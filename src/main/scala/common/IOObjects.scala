package pp202302.project.common

enum IOType:
  case StdIO
  case DummyIO(var input: String, var output: String)
