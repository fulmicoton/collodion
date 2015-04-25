React = require 'react'


Token = React.createClass
	
	render: ->
		className = "token token-" + @props.data.type.toLowerCase()
		<div className={className}>
			<div className="text">{@props.data.charterm}</div>
			<div className="stem">{@props.data.stem}</div>
			<div className="number">{@props.data.number}</div>
		</div>

EmptyToken = React.createClass

	render: ->
		<div className="empty-token">{@props.text}</div>


Staff = React.createClass

	url:->
	 	"http://localhost:8080/api/corpus/#{@props.docId}/processed"

	render: ->
		tokenEls = []
		curOffset = 0
		for token in @props.tokens
			tokenEls.push <Token data={token} />
			curOffset = token.offset.end
		<div>
			{tokenEls}
		</div>

module.exports = Staff
