React = require 'react'
store = require '../store.coffee'


annotationRank = 0
annotationHeight = 10

Token = React.createClass
	
	render: ->
		className = "token token-" + @props.data.type.toLowerCase()
		annotationsDiv = []
		for annotation in @props.data.annotations
			annotationsDiv.push <div className="annotation">{annotation.key}</div>
		<div className={className} style={height: @props.height}>
			<div className="text">{@props.data.charterm}</div>
			<div className="stem">{@props.data.stem}</div>
			<div className="number">{@props.data.number}</div>
			{annotationsDiv}
		</div>

EmptyToken = React.createClass
	render: ->
		<div className="empty-token">{@props.text}</div>


computeRequiredHeight = (tokens)->
	50 + 10 * computeMaxNbAnnotations(tokens)

computeMaxNbAnnotations = (tokens)->
	maxNbAnnotations = 0
	remainingAnnotations = {}
	annotationRank = 0
	for token in tokens
		# adding new annotation
		for annotation in token.annotations
			if remainingAnnotations[annotation.key]?
				remainingAnnotations[annotation.key] = Math.max(annotations[annotation.key], annotation.length)
			else
				remainingAnnotations[annotation.key] = remainingAnnotations.length
		newRemainingAnnotations = {}
		curTokenNbAnnotation = 0
		for annKey, remaining of remainingAnnotations
			curTokenNbAnnotation += 1
			if remaining > 1
				newRemainingAnnotations[annKey] = remaining - 1
		remainingAnnotations = newRemainingAnnotations
		maxNbAnnotations = Math.max(maxNbAnnotations, curTokenNbAnnotation)
	maxNbAnnotations


Staff = React.createClass
	
	getInitialState: ->
		tokens: []
	
	componentDidMount: ->
		@unbinders = []
		onChange = =>
			@setState store.getDoc()
		@unbinders.push store.events.docChange.bind(onChange)
	componentWillUnmount: ->
		for unbinder in @unbinders
			@unbinder()

	render: ->
		tokenEls = []
		curOffset = 0
		staffHeight = computeRequiredHeight @state.tokens
		for token in @state.tokens
			tokenEls.push <Token data={token} height={staffHeight}/>
			curOffset = token.offset.end
		<div className="staff">
			{tokenEls}
		</div>

module.exports = Staff
