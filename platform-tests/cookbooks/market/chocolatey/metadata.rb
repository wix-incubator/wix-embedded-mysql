name             'chocolatey'
maintainer       'Guilhem Lettron'
maintainer_email 'guilhem.lettron@youscribe.com'
license          'Apache 2.0'
description      'Install chocolatey and packages on Windows'
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version          '0.2.1'

supports 'windows'

depends 'windows', '~> 1.31'
