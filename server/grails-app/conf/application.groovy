

// Added by the Spring Security Core plugin: api/catAtleta
grails.plugin.springsecurity.userLookup.userDomainClassName = 'seguridad.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'seguridad.UserRole'
grails.plugin.springsecurity.authority.className = 'seguridad.Role'
grails.plugin.springsecurity.rest.token.storage.jwt.expiration = 14400

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	[pattern: '/**/ws/**',       access: ['permitAll']],
	[pattern: '/ws/categoriasAtleta',       access: ['permitAll']],
	[pattern: '/api/ws',     access: ['permitAll'], httpMethod: 'POST'],
	[pattern: '/api/ws/*',   access: ['permitAll'], httpMethod: 'GET'],
	[pattern: '/api/ws',     access: ['permitAll'], httpMethod: 'POST'],
	[pattern: '/api/*',     access: ['permitAll']],
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

