API_URL = "http://localhost:8080/api/"

class Resource
	
	url: (args...)-> 
		throw "Not Implemented"

	get: (cb)->
		url = @url()
		$.getJSON url, (data)=>
			@set data
			if cb?
				cb this

	set: (data)->
		$.extend this, data

	post: (cb)->
		url = @url()
		wrapped_cb = (data)=>
			if cb?
				cb (this)
		$.post url, @data, wrapped_cb, 'json'


class Corpus extends Resource

	url: ->
		API_URL + "corpus/"

	fetchDoc: (docId, cb)->
		doc = new Document(this, docId)
		doc.get (request)->
			cb(doc)
		doc

class Analyzer extends Resource

	url: ->
		API_URL + "analyzer/"

	refresh: (cb)->
		url = @url() + "reload/"
		$.getJSON url, cb

class Document extends Resource
	
	constructor: (@corpus, @docId)->

	url: ->
 		@corpus.url() + @docId + "/processed/"

module.exports = 
	Corpus: Corpus
	Document: Document
	Analyzer: Analyzer
